package coop.bancocredicoop.proyectos.guvpersistor.listener;

import coop.bancocredicoop.proyectos.guvpersistor.model.Jsoneable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "messages_topic", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }

    @KafkaListener(topics = "correcciones_topic", groupId = "json_group_id")
    public void consume(Jsoneable message) {
        System.out.println("Consumed message: " + message);
    }

}
