package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import coop.bancocredicoop.guv.persistor.services.KafkaProducer;
import coop.bancocredicoop.guv.persistor.utils.PipelineFunctions;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Try;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.vavr.API.*;

/**
 * Actor que se encarga de realizar las operaciones relacionadas con la correccion de un cheque.
 */
@Component("updateChequeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChequeActor extends AbstractActor {

    private final LoggingAdapter LOGGER = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    @Autowired
    private PipelineFunctions functions;

    @Autowired
    private KafkaProducer producer;

    public UpdateChequeActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(UpdateMessage.class, msg -> {
                Function2<Cheque, Cheque, Cheque> pipeline = msg.getType().isLeft() ?
                    correccionPipeline(msg) :
                    observacionPipeline(msg);

                this.service.update(pipeline, msg.getCheque())
                    .onFailure(this::logAndReturn)
                    .onSuccess((cheque) -> {
                        LOGGER.info("cheque con id {} fue actualizado correctamente, se procede a verificar el estado del deposito del cheque", msg.getCheque().getId());
                        //envia mensaje de verificacion de deposito a kafka para que el consumer de kafka delegue
                        //esta accion al post update actor
                        functions.mustExecutePostProcess.apply(msg)
                                .onFailure(ex -> LOGGER.warning(ex.getMessage()))
                                .onSuccess(result -> sendVerification(msg));
                    })
                    .onComplete((s) -> {
                        getSelf().tell("KILL-CHEQUE-ACTOR", getSelf());
                    });
            })
            .match(String.class, msg -> {
                LOGGER.info("Mensaje de envenenamiento: " + msg + " -> pildora recibida!");
                getSelf().tell(PoisonPill.getInstance(), getSelf());
            })
            .matchAny(o -> LOGGER.error("Tipo de mensaje desconocido"))
            .build();
    }

    /**
     * Envia un mensaje de verificacion a kafka para que sea enviado al backend de guv con el objeto de
     * verificar el estado de la corrección.
     * No se enviara dicho mensaje si se trata de una correccion en filial.
     *
     * @param msg mensaje
     */
    private void sendVerification(UpdateMessage msg){
        this.producer.sendVerificationMessage(msg.getCheque().getId(), msg.getToken())
            .onFailure(this::logAndReturn)
            .onSuccess((future) ->
                LOGGER.info("Mensaje de verificacion enviado a guv-backend para cheque con id {} de forma exitosa", msg.getCheque().getId())
            );
    }

    /**
     * Funcion que detalla el pipeline de ejecucion para realizar la actualizacion de un cheque.
     *
     * @param msg
     * @return
     */
    private Function2<Cheque, Cheque, Cheque> correccionPipeline(UpdateMessage msg) {
        LOGGER.info("Pipeline de correccion seteado para cheque con id {}", msg.getCheque().getId());
        Function2<Cheque, Cheque, Cheque> pipeline = Match(msg.getType().left().get()).of(
                Case($(TipoCorreccionEnum.IMPORTE), (type) -> functions.setImporteAndTruncado),
                Case($(TipoCorreccionEnum.CMC7), (type) -> functions.setCMC7),
                Case($(TipoCorreccionEnum.FECHA), (type) -> functions.setFecha),
                Case($(TipoCorreccionEnum.CUIT), (type) -> functions.setCuit),
                Case($(TipoCorreccionEnum.FILIAL), (type) -> functions.applyAll));
        return pipeline
                .andThen(functions.setCanjeInterno.curried().apply(msg.getCheque()))
                .andThen(functions.setStatus.curried().apply(msg.getCheque()))
                .andThen(functions.setFechaDiferidaAndCuit.curried().apply(msg.getCheque()));
    }

    /**
     * Funcion que detalla el pipeline de ejecucion para realizar la observacion de un cheque.
     *
     * @param msg
     * @return
     */
    private Function2<Cheque, Cheque, Cheque> observacionPipeline(UpdateMessage msg) {
        LOGGER.info("Pipeline de observacion seteado para cheque con id {}", msg.getCheque().getId());
        return functions.setObservacion.apply(msg.getType().right().get())
                .andThen(functions.setStatus.curried().apply(msg.getCheque()))
                .andThen(functions.setFechaDiferidaAndCuit.curried().apply(msg.getCheque()));
    }

    private void logAndReturn(Throwable e) {
        LOGGER.error("Error al actualizar los datos del cheque");
        LOGGER.error(e.getMessage());
    }
}
