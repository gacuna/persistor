package coop.bancocredicoop.guv.persistor.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.actors.VerifyMessage;
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

@Service
public class KafkaConsumer {

    @Autowired
    private ActorSystem system;

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(id = "correccionListener", topics = "${kafka.correccion.topic}", groupId = "${kafka.correccion.groupId}")
    public void consume(@Payload UpdateMessage message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Mensaje de actualizacion recibido en particion: {}, json: {}", partition, message.toString());
        final ActorRef updateActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
                .props("updateChequeActor"), "updateActor_" + UUID.randomUUID());
        updateActor.tell(message, ActorRef.noSender()); //fire and forget pattern!
    }

    @KafkaListener(id = "verificacionListener", topics = "${kafka.verificacion.topic}", groupId = "${kafka.verificacion.groupId}", containerFactory = "verifyMessageConcurrentKafkaListenerContainerFactory")
    public void consume(@Payload VerifyMessage message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        LOGGER.info("Mensaje de verificación recibido en particion: {}, json: {}", partition, message.toString());
        final ActorRef postUpdateActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
                .props("postUpdateActor"), "postUpdateActor_" + UUID.randomUUID());
        postUpdateActor.tell(message, ActorRef.noSender()); //fire and forget pattern!
    }

}
