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
import io.vavr.Function2;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.vavr.API.*;

@Component("updateChequeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChequeActor extends AbstractActor {

    private final LoggingAdapter LOGGER = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    @Autowired
    private PipelineFunctions pipeline;

    @Autowired
    private KafkaProducer producer;

    public UpdateChequeActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(UpdateMessage.class, msg -> {
                Either<TipoCorreccionEnum, Cheque.Observacion> type = msg.getType().isLeft() ?
                        Left(msg.getType().left().get()) :
                        Right(msg.getType().right().get());

                Function2<Cheque, Cheque, Cheque> pipeline = msg.getType().isLeft() ?
                    correccionPipeline(msg) :
                    observacionPipeline(msg);

                this.service.update(pipeline, msg.getCheque())
                    .onFailure(this::logAndReturn)
                    .onSuccess((cheque) -> {
                        LOGGER.info("cheque con id {} fue actualizado correctamente, se procede a verificar el estado del deposito {}",
                                cheque.getId(), cheque.getDeposito().getId());
                        //envia mensaje de verificacion de deposito a kafka para que el consumer de kafka delegue
                        //esta accion al post update actor
                        this.producer.sendVerificationMessage(type, cheque, msg.getToken());
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
     * Funcion que detalla el pipeline de ejecucion para realizar la actualizacion de un cheque.
     *
     * @param msg
     * @return
     */
    private Function2<Cheque, Cheque, Cheque> correccionPipeline(UpdateMessage msg) {
        return Match(msg.getType().left().get()).of(
                Case($(TipoCorreccionEnum.IMPORTE), (type) -> pipeline.setImporteAndTruncado),
                Case($(TipoCorreccionEnum.CMC7), (type) -> pipeline.setCMC7),
                Case($(TipoCorreccionEnum.FECHA), (type) -> pipeline.setFecha),
                Case($(TipoCorreccionEnum.CUIT), (type) -> pipeline.setCuit))
                .andThen(pipeline.setStatus.curried().apply(msg.getCheque()))
                .andThen(pipeline.setFechaDiferidaAndCuit.curried().apply(msg.getCheque()));
    }

    /**
     * Funcion que detalla el pipeline de ejecucion para realizar la observacion de un cheque.
     *
     * @param msg
     * @return
     */
    private Function2<Cheque, Cheque, Cheque> observacionPipeline(UpdateMessage msg) {
        return pipeline.setObservacion.apply(msg.getType().right().get())
                .andThen(pipeline.setStatus.curried().apply(msg.getCheque()))
                .andThen(pipeline.setFechaDiferidaAndCuit.curried().apply(msg.getCheque()));
    }

    private void logAndReturn(Throwable e) {
        LOGGER.error("Error al actualizar los datos del cheque");
        LOGGER.error(e.getMessage());
    }
}
