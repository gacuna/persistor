package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import org.springframework.util.ObjectUtils;

public class UpdateMessage {

    private MessageType<TipoCorreccionEnum, Cheque.Observacion> type;
    private Cheque cheque;
    private String token;

    public UpdateMessage() {}

    public UpdateMessage(MessageType<TipoCorreccionEnum, Cheque.Observacion> type, Cheque cheque, String token) {
        this.type= type;
        this.cheque = cheque;
        this.token = token;
    }

    public MessageType<TipoCorreccionEnum, Cheque.Observacion> getType() {
        return type;
    }

    public Cheque getCheque() {
        return cheque;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "UpdateMessage{" +
                "tipoCorreccion='" + ObjectUtils.nullSafeToString(type)  + '\'' +
                ", cheque='" + cheque.getId() + '\'' +
                ", token='" + ObjectUtils.nullSafeToString(token) + '\'' +
                '}';
    }
}