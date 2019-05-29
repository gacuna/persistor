package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Actor que se encarga de realizar las operaciones relacionadas al balanceo de un deposito.
 *
 */
@Component("balanceActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BalanceActor extends AbstractActor {

    private final LoggingAdapter LOGGER = Logging.getLogger(getContext().getSystem(), this);

    public BalanceActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(BalanceMessage.class, msg -> {
                //TODO Hacer que esta cosa balancee..

                getSelf().tell("KILL-BALANCE-ACTOR", getSelf());
            })
            .match(String.class, msg -> {
                LOGGER.info("Mensaje de envenenamiento: " + msg + " -> pildora recibida!");
                getSelf().tell(PoisonPill.getInstance(), getSelf());
            })
            .matchAny(o -> LOGGER.error("Tipo de mensaje desconocido"))
            .build();
    }

}
