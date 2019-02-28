package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

public class UpdateMessage {
    private String type;
    private Correccion correccion;
    private String token;

    public UpdateMessage() {}

    public UpdateMessage(String type, Correccion correccion, String token) {
        this.type = type;
        this.correccion = correccion;
        this.token = token;
    }

    public String getType() {
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
        return "UpdateMessage{" +
                "type='" + type + '\'' +
                ", correccion='" + correccion.toString() + '\'' +
                ", token='" + type + '\'' +
                '}';
    }
}