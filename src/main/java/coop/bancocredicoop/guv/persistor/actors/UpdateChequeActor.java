package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("updateChequeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChequeActor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    public UpdateChequeActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(UpdateMessage.class, msg -> {
            logger.info("Mensaje de actualizacion de importe recibo");
            //TODO Switch o pattern matching ???? preguntar a Martin
            //TODO Llamar al servicio que haga update
            this.service.update(msg.getCorreccion());
            getSelf().tell(PoisonPill.getInstance(), getSelf());
        })
        .matchAny(o -> logger.error("Tipo de mensaje desconocido"))
        .build();
    }

}
