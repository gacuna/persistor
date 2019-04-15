package coop.bancocredicoop.guv.persistor.controllers.api;

import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import coop.bancocredicoop.guv.persistor.utils.CorreccionUtils;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

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
        //MDC.put("user", "bender");
        log.info("Iniciando correccion de tipo {} del cheque con id {}", type, correccion.getId());

        TipoCorreccionEnum tipoCorreccionEnum = TipoCorreccionEnum.valueOf(type);

        Function1<Correccion, Try<Correccion>> f =
            Match(tipoCorreccionEnum).of(
                Case($(TipoCorreccionEnum.IMPORTE), this.correccionService.truncarSiSuperaImporteTruncamiento),
                Case($(TipoCorreccionEnum.CMC7), this.correccionService.superaReintentosValidos),
                Case($(TipoCorreccionEnum.FECHA), this.correccionService.defaultValidation),
                Case($(TipoCorreccionEnum.CUIT), this.correccionService.defaultValidation)
            );

        Try<Correccion> correccionTry = this.correccionService.validateAndApply(tipoCorreccionEnum, f, correccion);

        correccionTry.onFailure((ex) -> {
            log.error("Error de validación en la correccion del cheque con id {}, detalle: {}", correccion.getId(), ex.getMessage());
        });

        correccionTry.onSuccess(correccionValidated -> this.template.send(this.topic, new UpdateMessage(tipoCorreccionEnum, correccionValidated, token)))
        .onFailure(ex -> log.error("Error al enviar mensaje de actualizacion del cheque con id {}, detalle: {}", correccion.getId(), ex.getMessage()))
        .onSuccess(correccionSent -> log.info("Mensaje de actualizacion del cheque con id {}, detalle: Enviado OK", correccionSent.getId()));

        return Mono.just("OK");
    }

}
