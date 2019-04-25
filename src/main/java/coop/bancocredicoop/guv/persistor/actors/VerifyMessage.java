package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import io.vavr.control.Either;

public class VerifyMessage {
    private Either<TipoCorreccionEnum, Cheque.Observacion> type;
    private Cheque cheque;
    private String token;

    public VerifyMessage() {}

    public VerifyMessage(Either<TipoCorreccionEnum, Cheque.Observacion> type, Cheque cheque, String token) {
        this.type = type;
        this.cheque = cheque;
        this.token = token;
    }

    public Either<TipoCorreccionEnum, Cheque.Observacion> getType() {
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
                "type='" + type.map(__ -> __.name()) + '\'' +
                ", cheque='" + cheque.toString() + '\'' +
                ", token='" + type + '\'' +
                '}';
    }
}