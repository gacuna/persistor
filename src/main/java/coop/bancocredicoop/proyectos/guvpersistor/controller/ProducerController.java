package coop.bancocredicoop.proyectos.guvpersistor.controller;

import coop.bancocredicoop.proyectos.guvpersistor.model.Cheque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    private static final String JSON_TOPIC = "correcciones_topic";

    @Autowired
    private KafkaTemplate<String, Cheque> kafkaJsonTemplate;

    @PostMapping("/sendJson")
    public Mono<String> produceJson(@RequestBody Cheque cheque) {
        this.kafkaJsonTemplate.send(JSON_TOPIC, cheque);
        return Mono.just("json message was sent");
    }

}
