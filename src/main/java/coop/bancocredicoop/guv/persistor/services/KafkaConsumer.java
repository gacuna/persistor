package coop.bancocredicoop.guv.persistor.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static coop.bancocredicoop.guv.persistor.utils.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Service
public class KafkaConsumer {

    @Autowired
    private ActorSystem system;

    @KafkaListener(topics = "correccion_topic", groupId = "${kafka.groupId}")
    public void consume(UpdateMessage message) {
        System.out.println("Tipo de mensaje de update recibido: " + message.getType() + ", json: " + message.toString());
        final ActorRef updateActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
                .props("updateChequeActor"), "updateActor_" + UUID.randomUUID());
        updateActor.tell(message, ActorRef.noSender());
    }

}
