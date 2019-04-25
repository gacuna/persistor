package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

public class UpdateMessage {
    private TipoCorreccionEnum type;
    private Correccion correccion;
    private Boolean mustObserve;
    private String token;

    public UpdateMessage() {}

    public UpdateMessage(TipoCorreccionEnum type, Correccion correccion, Boolean mustObserve, String token) {
        this.type = type;
        this.correccion = correccion;
        this.mustObserve = mustObserve;
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

    public Boolean mustObserve() {
        return mustObserve;
    }

    @Override
    public String toString() {
        return "UpdateMessage{" +
                "type='" + type.toString() + '\'' +
                ", correccion='" + correccion.toString() + '\'' +
                ", mustObserve='" + mustObserve.toString() + '\'' +
                ", token='" + type + '\'' +
                '}';
    }
}