package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import io.vavr.Function1;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.vavr.API.*;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

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
                Try<String> _try = Match(msg.getType()).of(
                        Case($("importe"), (o) -> this.service.update(msg.getCorreccion())),
                        Case($("cmc7"), (o) -> this.service.update(msg.getCorreccion())),
                        Case($("fecha"), (o) -> this.service.update(msg.getCorreccion())),
                        Case($("cuit"), (o) -> this.service.update(msg.getCorreccion()))
                );

                Match(_try).of(
                        Case($Success($()), o -> {
                            logger.info("update service OK");
                            final ActorRef postUpdateActor = system.actorOf(
                                    SPRING_EXTENSION_PROVIDER.get(system).props("postUpdateActor"),
                                    "postUpdateActor_" + UUID.randomUUID());
                            postUpdateActor.tell(msg, ActorRef.noSender());
                            return Success("OK");
                        }),
                        Case($Failure($()), o -> {
                            logger.error("update service ERROR", o);
                            return Failure(o);
                        })
                ).andFinally(() -> getSelf().tell("KILL-CHEQUE-ACTOR", getSelf()));
            })
            .match(String.class, msg -> {
                logger.info("Mensaje de envenenamiento: " + msg + " -> pildora recibida!");
                getSelf().tell(PoisonPill.getInstance(), getSelf());
            })
            .matchAny(o -> logger.error("Tipo de mensaje desconocido"))
            .build();
    }

}
