package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.actors.ObserveMessage;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.actors.VerifyMessage;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaProducer {

    @Value(value = "${kafka.verificacion.topic}")
    private String verificacionTopic;

    @Value(value = "${kafka.correccion.topic}")
    private String correccionTopic;

    @Autowired
    private KafkaTemplate<String, UpdateMessage> updateMessageTemplate;

    @Autowired
    private KafkaTemplate<String, VerifyMessage> verificationMessageTemplate;

    public Try<ListenableFuture<SendResult<String, UpdateMessage>>> sendUpdateMessage(TipoCorreccionEnum tipoCorreccionEnum, Correccion correccion, String token) {
        return Try.of(() ->
            this.updateMessageTemplate.send(this.correccionTopic, new UpdateMessage(tipoCorreccionEnum, correccion, token))
        );
    }

    public Try<ListenableFuture<SendResult<String, VerifyMessage>>> sendVerificationMessage(TipoCorreccionEnum tipoCorreccionEnum, Cheque cheque, String token) {
        return Try.of(() ->
                this.verificationMessageTemplate.send(this.verificacionTopic, new VerifyMessage(tipoCorreccionEnum, cheque, token)));
    }

}
