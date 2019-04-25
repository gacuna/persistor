package coop.bancocredicoop.guv.persistor.controllers.api;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import coop.bancocredicoop.guv.persistor.services.KafkaProducer;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/api/persistor")
public class PersistorController {

    @Autowired
    private CorreccionService correccionService;

    @Autowired
    private KafkaProducer producer;

    private static Logger log = LoggerFactory.getLogger(PersistorController.class);

    @PostMapping("/{type}")
    public Mono<String> save(@PathVariable String type,
                             @RequestBody Correccion correccion,
                             @RequestHeader(CorreccionService.GUV_AUTH_TOKEN) String token) {
        //MDC.put("user", "bender");
        log.info("Iniciando correccion de tipo {} del cheque con id {}", type, correccion.getId());

        this.producer.sendUpdateMessage(Either.left(TipoCorreccionEnum.valueOf(type)), correccion, Option.of(token))
            .onFailure(ex -> log.error("Error al enviar mensaje de actualizacion del cheque con id {}, detalle: {}", correccion.getId(), ex.getMessage()))
            .onSuccess(future -> log.info("Mensaje de actualizacion del cheque con id {} fue enviado correctamente a kafka", correccion.getId()));

        return Mono.just("OK");
    }

    @PostMapping("/observation/{type}")
    public Mono<String> observe(@PathVariable String type,
                                @RequestBody Correccion correccion) {
        log.info("Iniciando observacion de tipo {} del cheque con id {}", type, correccion.getId());

        this.producer.sendUpdateMessage(Either.right(Cheque.Observacion.valueOf(type)), correccion, Option.none())
                .onFailure(ex -> log.error("Error al enviar mensaje de observacion del cheque con id {}, detalle: {}", correccion.getId(), ex.getMessage()))
                .onSuccess(future -> log.info("Mensaje de observacion del cheque con id {} fue enviado correctamente a kafka", correccion.getId()));
        return Mono.just("OK");
    }

}
