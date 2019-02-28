package coop.bancocredicoop.guv.persistor.utils;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import io.vavr.Function2;

public final class CorreccionUtils {

    public static Function2<Correccion, Cheque, Cheque> updateImporte = (correccion, cheque) -> {
        cheque.setTruncado(correccion.getTruncado());
        cheque.setImporte(correccion.getImporte());
        return cheque;
    };

    public static Function2<Correccion, Cheque, Cheque> updateCmc7 = (correccion, cheque) -> {
        cheque.setCmc7(correccion.getCmc7());
        return cheque;
    };

    public static Function2<Correccion, Cheque, Cheque> updateFecha = (correccion, cheque) -> {
        cheque.setFechaDiferida(correccion.getFechaDiferida());
        return cheque;
    };

    public static Function2<Correccion, Cheque, Cheque> updateCuit = (correccion, cheque) -> {
        cheque.setCuit(correccion.getCuit());
        return cheque;
    };

}
