package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.vavr.API.*;

import java.util.UUID;

import static coop.bancocredicoop.guv.persistor.utils.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Component("updateChequeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChequeActor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    @Autowired
    private ActorSystem system;

    public UpdateChequeActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(UpdateMessage.class, msg -> {
                logger.info("Mensaje de actualizacion de importe recibo");
                Match(msg.getType()).of(
                        Case($("importe"), (o) -> this.service.update(this.service.updateImporte, msg.getCorreccion())),
                        Case($("cmc7"), (o) -> this.service.update(this.service.updateCmc7, msg.getCorreccion())),
                        Case($("fecha"), (o) -> this.service.update(this.service.updateFecha, msg.getCorreccion())),
                        Case($("cuit"), (o) -> this.service.update(this.service.updateCuit, msg.getCorreccion())))
                .onFailure((e) -> logAndReturn(e))
                .onSuccess((cheque) -> logger.info("cheque con id {} fue actualizado correctamente", cheque.getId()))
                .onComplete((s) -> {
                    final ActorRef postUpdateActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("postUpdateActor"), "postUpdateActor_" + UUID.randomUUID());
                    postUpdateActor.tell(msg, ActorRef.noSender());
                    getSelf().tell("KILL-CHEQUE-ACTOR", getSelf());
                });
            })
            .match(String.class, msg -> {
                logger.info("Mensaje de envenenamiento: " + msg + " -> pildora recibida!");
                getSelf().tell(PoisonPill.getInstance(), getSelf());
            })
            .matchAny(o -> logger.error("Tipo de mensaje desconocido"))
            .build();
    }

    private String logAndReturn(Throwable e) {
        logger.error("Error al actualizar los datos del cheque");
        logger.error(e.getMessage());
        return "OK";
    }
}
