package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "correcciones_topic", groupId = "${kafka.groupId}")
    public void consume(Correccion message) {
        System.out.println("Consumed message: " + message);
    }
}
