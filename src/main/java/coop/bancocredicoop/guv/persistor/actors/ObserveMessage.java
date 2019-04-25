package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

public class ObserveMessage {
    private TipoCorreccionEnum type;
    private Correccion correccion;

    public ObserveMessage() {}

    public ObserveMessage(TipoCorreccionEnum type, Correccion correccion) {
        this.type = type;
        this.correccion = correccion;
    }

    public TipoCorreccionEnum getType() {
        return type;
    }

    public Correccion getCorreccion() {
        return correccion;
    }

    @Override
    public String toString() {
        return "ObserveMessage{" +
                "type='" + type.toString() + '\'' +
                ", correccion='" + correccion.toString() + '\'' +
                '}';
    }
}