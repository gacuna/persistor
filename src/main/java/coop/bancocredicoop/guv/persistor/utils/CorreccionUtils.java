package coop.bancocredicoop.guv.persistor.utils;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import io.vavr.Function2;

public final class CorreccionUtils {

    public final static String GUV_AUTH_TOKEN = "GUV-AUTH-TOKEN";

    public static Function2<Correccion, Cheque, Cheque> setImporteAndTruncado = (correccion, cheque) -> {
        cheque.setTruncado(correccion.getTruncado());
        cheque.setImporte(correccion.getImporte());
        return cheque;
    };

    public static Function2<Correccion, Cheque, Cheque> setCMC7 = (correccion, cheque) -> {
        cheque.setCmc7(correccion.getCmc7());
        return cheque;
    };

    public static Function2<Correccion, Cheque, Cheque> setFecha = (correccion, cheque) -> {
        cheque.setFechaDiferida(correccion.getFechaDiferida());
        return cheque;
    };

    public static Function2<Correccion, Cheque, Cheque> setCuit = (correccion, cheque) -> {
        cheque.setCuit(correccion.getCuit());
        return cheque;
    };

}
