package coop.bancocredicoop.guv.persistor.actors;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import io.vavr.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Component("postUpdateActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PostUpdateActor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Autowired
    private CorreccionService service;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(VerifyMessage.class, msg -> {
                    this.service.postSaveBackgroundPost(msg.getId(), msg.getToken())
                        .recover(e -> API.Match(e).of(
                            Case($(instanceOf(Exception.class)), te -> logAndReturnStatus(te))
                        ))
                        .andFinally(() -> getSelf().tell(PoisonPill.getInstance(), getSelf()));
                })
                .matchAny(o -> {
                    logger.error("Tipo de mensaje desconocido");
                    getSelf().tell(PoisonPill.getInstance(), getSelf());
                })
                .build();
    }

    private Integer logAndReturnStatus(Exception e) {
        logger.error("Error al enviar request al servicio de verificacion de deposito");
        logger.error(e.getMessage());
        return -1;
    }
}
