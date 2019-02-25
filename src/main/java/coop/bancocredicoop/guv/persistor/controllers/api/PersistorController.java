package coop.bancocredicoop.guv.persistor.controllers.api;

import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionImporte;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/api/persistor")
public class PersistorController {

    @Autowired
    private CorreccionService correccionService;

    @Autowired
    private KafkaTemplate<String, Correccion> template;

    private static Logger log = LoggerFactory.getLogger(PersistorController.class);

    @PostMapping
    public Mono<String> save(@RequestBody CorreccionImporte correccion) {
        this.template.send("correccion_topic", correccion);
        return Mono.just("OK");
    }

}
