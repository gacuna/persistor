package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PostUpdateActor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AfterImporteUpdateMessage.class, msg -> {
                    //TODO Llamar al servicio de post procesamiento adecuado, por ej. para ejecutar un post al backend de guv
                })
                .matchAny(o -> logger.error("Tipo de mensaje desconocido"))
                .build();
    }

    public static class AfterImporteUpdateMessage {}
    public static class AfterCuitUpdateMessage {}
    public static class AfterCMC7UpdateMessage {}
    public static class AfterFechaUpdateMessage {}

}
