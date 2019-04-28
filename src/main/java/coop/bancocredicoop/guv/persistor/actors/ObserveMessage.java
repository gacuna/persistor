package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;

public class ObserveMessage {
    private Cheque.Observacion type;
    private Cheque cheque;

    public ObserveMessage() {}

    public ObserveMessage(Cheque.Observacion type, Cheque cheque) {
        this.type = type;
        this.cheque = cheque;
    }

    public Cheque.Observacion getType() {
        return type;
    }

    public Cheque getCorreccion() {
        return cheque;
    }

    @Override
    public String toString() {
        return "ObserveMessage{" +
                "type='" + type.toString() + '\'' +
                ", cheque='" + cheque.toString() + '\'' +
                '}';
    }
}