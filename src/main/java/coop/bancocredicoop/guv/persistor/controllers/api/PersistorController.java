package coop.bancocredicoop.guv.persistor.controllers.api;

import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import coop.bancocredicoop.guv.persistor.utils.CorreccionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private KafkaTemplate<String, UpdateMessage> template;

    @Value(value = "${kafka.topic}")
    private String topic;

    private static Logger log = LoggerFactory.getLogger(PersistorController.class);

    @PostMapping("/{type}")
    public Mono<String> save(@PathVariable String type,
                             @RequestBody Correccion correccion,
                             @RequestHeader(CorreccionUtils.GUV_AUTH_TOKEN) String token) {
        correccion = this.correccionService.chequearTruncamientoAndApply(type, this.correccionService.truncarSiSuperaImporteTruncamiento, correccion);
        this.template.send(this.topic, new UpdateMessage(type, correccion, token));
        return Mono.just("OK");
    }

}
