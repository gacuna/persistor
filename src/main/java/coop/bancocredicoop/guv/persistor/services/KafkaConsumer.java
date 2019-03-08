package coop.bancocredicoop.guv.persistor.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value(value = "${kafka.topic}")
    private String topic;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.groupId}")
    public void consume(@Payload UpdateMessage message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Mensaje de actualizacion recibido en particion: " + partition + ", json: " + message.toString());
        final ActorRef updateActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
                .props("updateChequeActor"), "updateActor_" + UUID.randomUUID());
        updateActor.tell(message, ActorRef.noSender()); //fire and forget pattern!
    }

}
