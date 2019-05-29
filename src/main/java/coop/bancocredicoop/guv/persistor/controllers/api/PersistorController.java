package coop.bancocredicoop.guv.persistor.controllers.api;

import coop.bancocredicoop.guv.persistor.actors.MessageType;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.Deposito;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import coop.bancocredicoop.guv.persistor.services.KafkaProducer;
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
    private KafkaProducer producer;

    private static Logger log = LoggerFactory.getLogger(PersistorController.class);

    @PostMapping("/{type}")
    public Mono<String> save(@PathVariable String type,
                             @RequestBody Cheque cheque,
                             @RequestHeader(CorreccionService.GUV_AUTH_TOKEN) String token) {
        //MDC.put("user", "bender");
        log.info("Iniciando correccion de tipo {} del cheque con id {}", type, cheque.getId());
        this.producer.sendUpdateMessage(buildMessageType(type, true), cheque, token)
            .onFailure(ex -> log.error("Error al enviar mensaje de actualizacion del cheque con id {}, detalle: {}", cheque.getId(), ex.getMessage()))
            .onSuccess(future -> log.info("Mensaje de actualizacion del cheque con id {} fue enviado correctamente a kafka", cheque.getId()));

        return Mono.just("OK");
    }

    @PostMapping("/observation/{type}")
    public Mono<String> observe(@PathVariable String type,
                                @RequestBody Cheque cheque) {
        log.info("Iniciando observacion de tipo {} del cheque con id {}", type, cheque.getId());

        this.producer.sendUpdateMessage(buildMessageType(type, false), cheque, "")
                .onFailure(ex -> log.error("Error al enviar mensaje de observacion del cheque con id {}, detalle: {}", cheque.getId(), ex.getMessage()))
                .onSuccess(future -> log.info("Mensaje de observacion del cheque con id {} fue enviado correctamente a kafka", cheque.getId()));

        return Mono.just("OK");
    }

    @PostMapping("/balance")
    public Mono<String> balance(@RequestBody Deposito deposito) {
        log.info("Iniciando balanceo del deposito con id {}", deposito.getId());

        this.producer.sendBalanceMessage(deposito)
                .onFailure(ex -> log.error("Error al enviar mensaje de balanceo del deposito con id {}, detalle: {}", deposito.getId(), ex.getMessage()))
                .onSuccess(future -> log.info("Mensaje de balanceo del deposito con id {} fue enviado correctamente a kafka", deposito.getId()));

        return Mono.just("OK");
    }


    private MessageType<TipoCorreccionEnum, Cheque.Observacion> buildMessageType(String name, boolean isLeft) {
        MessageType<TipoCorreccionEnum, Cheque.Observacion> messageType = new MessageType<>();
        if (isLeft)
            messageType.setLeft(TipoCorreccionEnum.valueOf(name.toUpperCase()));
        else
            messageType.setRight(Cheque.Observacion.valueOf(name.toUpperCase()));
        return messageType;
    }

}
