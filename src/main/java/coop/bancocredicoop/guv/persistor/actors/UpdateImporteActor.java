package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateImporteActor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    public UpdateImporteActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(UpdateImporteMessage.class, msg -> {
            logger.info("Mensaje de actualizacion de importe recibo");
            //TODO Llamar al servicio que haga update
            getSender().tell("OK", getSelf());
        })
        .match(String.class, msg -> {
            logger.info("Mensaje recibido: " + msg);
        })
        .matchAny(o -> logger.error("Tipo de mensaje desconocido"))
        .build();
    }

    public static class UpdateImporteMessage {}
}
