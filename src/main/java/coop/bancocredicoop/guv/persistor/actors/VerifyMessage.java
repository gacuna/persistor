package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

public class VerifyMessage {
    private TipoCorreccionEnum type;
    private Correccion correccion;
    private String token;

    public VerifyMessage() {}

    public VerifyMessage(TipoCorreccionEnum type, Correccion correccion, String token) {
        this.type = type;
        this.correccion = correccion;
        this.token = token;
    }

    public TipoCorreccionEnum getType() {
        return type;
    }

    public Correccion getCorreccion() {
        return correccion;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "VerifyMessage{" +
                "type='" + type.toString() + '\'' +
                ", correccion='" + correccion.toString() + '\'' +
                ", token='" + type + '\'' +
                '}';
    }
}