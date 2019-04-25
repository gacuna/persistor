package coop.bancocredicoop.guv.persistor.actors;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import io.vavr.control.Either;
import io.vavr.control.Option;

public class UpdateMessage {
    private Either<TipoCorreccionEnum, Cheque.Observacion> type;
    private Correccion correccion;
    private Option<String> token;

    public UpdateMessage() {}

    public UpdateMessage(Either<TipoCorreccionEnum, Cheque.Observacion> type, Correccion correccion, Option<String> token) {
        this.type= type;
        this.correccion = correccion;
        this.token = token;
    }

    public Either<TipoCorreccionEnum, Cheque.Observacion> getType() {
        return type;
    }

    public Correccion getCorreccion() {
        return correccion;
    }

    public Option<String> getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "UpdateMessage{" +
                "tipoCorreccion='" + type.toString() + '\'' +
                ", correccion='" + correccion.toString() + '\'' +
                ", token='" + token.getOrNull() + '\'' +
                '}';
    }
}