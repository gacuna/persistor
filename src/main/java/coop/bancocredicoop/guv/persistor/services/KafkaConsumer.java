package coop.bancocredicoop.guv.persistor.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.actors.VerifyMessage;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static coop.bancocredicoop.guv.persistor.utils.SpringExtension.SPRING_EXTENSION_PROVIDER;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Service
public class KafkaConsumer {

    @Autowired
    private ActorSystem system;

    @Autowired
    private CorreccionService correccionService;

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "${kafka.correccion.topic}", groupId = "${kafka.groupId}")
    public void consume(@Payload UpdateMessage message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Mensaje de actualizacion recibido en particion: {}, json: {}", partition, message.toString());
        final ActorRef updateActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
                .props("updateChequeActor"), "updateActor_" + UUID.randomUUID());
        updateActor.tell(message, ActorRef.noSender()); //fire and forget pattern!
    }

    @KafkaListener(topics = "${kafka.verificacion.topic}", groupId = "${kafka.groupId}")
    public void consume(@Payload VerifyMessage message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Mensaje de verificación recibido en particion: {}, json: {}", partition, message.toString());
        Cheque cheque = Cheque.of(message.getCorreccion());
        Try<Integer> post = this.correccionService.saveCorreccionBackgroundPost(cheque, message.getToken());
        post.onFailure(ex -> LOGGER.error("Error al enviar el mensaje de actualizacion al backend para el cheque con id {}, detalle: ", cheque.getId(), ex.getMessage()));
        post.onSuccess(status -> LOGGER.info("Se envio correctamente el mensaje de actualizacion al backend para cheque con id {}", cheque.getId()));
    }

}
