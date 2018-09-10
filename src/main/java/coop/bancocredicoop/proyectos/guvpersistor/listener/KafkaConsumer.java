package coop.bancocredicoop.proyectos.guvpersistor.listener;

import com.fasterxml.jackson.annotation.JsonView;
import coop.bancocredicoop.proyectos.guvpersistor.model.Cheque;
import coop.bancocredicoop.proyectos.guvpersistor.model.Jsoneable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "correcciones_topic", groupId = "json_group_id")
    public void consume(Cheque cheque) {
        System.out.println(new Date().toInstant().toString() + " - Cheque recibido id: " +  cheque.getId() + ", cmc7: " + cheque.getCmc7());
    }

}
