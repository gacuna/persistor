package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import coop.bancocredicoop.guv.persistor.services.KafkaProducer;
import io.vavr.Function2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.vavr.API.*;

import java.util.UUID;

import static coop.bancocredicoop.guv.persistor.utils.SpringExtension.SPRING_EXTENSION_PROVIDER;
import static io.vavr.Predicates.instanceOf;

@Component("updateChequeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChequeActor extends AbstractActor {

    private final LoggingAdapter LOGGER = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    @Autowired
    private KafkaProducer producer;

    public UpdateChequeActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(UpdateMessage.class, msg -> {
                boolean mustObserve = msg.getType().isLeft(); //left = msg correccion| right = msg observacion
                Function2<Correccion, Cheque, Cheque> decorator = Match(mustObserve).of(
                    Case($(Boolean.TRUE), correccionPipeline(msg)),
                    Case($(Boolean.FALSE), observacionPipeline(msg))
                );

                this.service.update(decorator, msg.getCorreccion())
                    .onFailure(this::logAndReturn)
                    .onSuccess((cheque) -> {
                        LOGGER.info("cheque con id {} fue actualizado correctamente, se procede a verificar el estado del deposito {}",
                                cheque.getId(), cheque.getDeposito().getId());
                        //envia mensaje de verificacion de deposito a kafka para que el consumer de kafka delegue
                        //esta accion al post update actor
                        this.producer.sendVerificationMessage(msg.getType(), cheque, msg.getToken().get());
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
    private Function2<Correccion, Cheque, Cheque> correccionPipeline(UpdateMessage msg) {
        return Match(msg.getType().getLeft()).of(
                Case($(TipoCorreccionEnum.IMPORTE), (type) -> service.setImporteAndTruncado),
                Case($(TipoCorreccionEnum.CMC7), (type) -> service.setCMC7),
                Case($(TipoCorreccionEnum.FECHA), (type) -> service.setFecha),
                Case($(TipoCorreccionEnum.CUIT), (type) -> service.setCuit))
                .andThen(service.setStatus.curried().apply(msg.getCorreccion()))
                .andThen(service.setFechaDiferidaAndCuit.curried().apply(msg.getCorreccion()));
    }

    /**
     * Funcion que detalla el pipeline de ejecucion para realizar la observacion de un cheque.
     *
     * @param msg
     * @return
     */
    private Function2<Correccion, Cheque, Cheque> observacionPipeline(UpdateMessage msg) {
        return service.setObservacion.apply(msg.getType().get())
                .andThen(service.setStatus.curried().apply(msg.getCorreccion()))
                .andThen(service.setFechaDiferidaAndCuit.curried().apply(msg.getCorreccion()));
    }

    private void logAndReturn(Throwable e) {
        LOGGER.error("Error al actualizar los datos del cheque");
        LOGGER.error(e.getMessage());
    }
}
