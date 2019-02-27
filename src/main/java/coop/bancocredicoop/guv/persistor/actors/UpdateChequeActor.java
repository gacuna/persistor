package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
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

@Component("updateChequeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChequeActor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    public UpdateChequeActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(UpdateMessage.class, msg -> {
                Try<String> _try = Match(msg.getType()).of(
                        Case($("importe"), (o) -> this.service.update(msg.getCorreccion())),
                        Case($("cmc7"), (o) -> this.service.update(msg.getCorreccion()))
                );

                Match(_try).of(
                        Case($Success($()), o -> {
                            logger.info("update service OK");
                            return x.apply("OK");
                        }),
                        Case($Failure($()), o -> {
                            logger.error("update service ERROR", o);
                            return x.apply("ERROR");
                        })
                ).map(x -> {
                    logger.info("Mensaje de actualizacion de importe recibo");
                    getSelf().tell("KILL-CHEQUE-ACTOR", getSelf());
                    return x;
                });
            })
            .match(String.class, msg -> {
                logger.info("Mensaje de envenenamiento: " + msg + " -> pildora recibida!");
                getSelf().tell(PoisonPill.getInstance(), getSelf());
            })
            .matchAny(o -> logger.error("Tipo de mensaje desconocido"))
            .build();
    }

    private Function1<String, Option<String>> x = (status) -> Some(status);

}
