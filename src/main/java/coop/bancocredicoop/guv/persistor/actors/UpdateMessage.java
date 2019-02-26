package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

public class UpdateMessage {
    private String type;
    private Correccion correccion;

    public UpdateMessage() {}

    public UpdateMessage(String type, Correccion correccion) {
        this.type = type;
        this.correccion = correccion;
    }

    public String getType() {
        return type;
    }

    public Correccion getCorreccion() {
        return correccion;
    }

    @Override
    public String toString() {
        return "UpdateMessage{" +
                "type='" + type + '\'' +
                ", correccion=" + correccion.toString() +
                '}';
    }
}