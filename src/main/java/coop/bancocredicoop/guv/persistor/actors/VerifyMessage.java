package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

public class VerifyMessage {
    private TipoCorreccionEnum type;
    private Cheque cheque;
    private String token;

    public VerifyMessage() {}

    public VerifyMessage(TipoCorreccionEnum type, Cheque cheque, String token) {
        this.type = type;
        this.cheque = cheque;
        this.token = token;
    }

    public TipoCorreccionEnum getType() {
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
        return "VerifyMessage{" +
                "type='" + type.toString() + '\'' +
                ", cheque='" + cheque.toString() + '\'' +
                ", token='" + type + '\'' +
                '}';
    }
}