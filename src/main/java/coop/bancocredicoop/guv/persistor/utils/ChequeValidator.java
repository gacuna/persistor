package coop.bancocredicoop.guv.persistor.utils;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.EstadoCheque;
import io.vavr.control.Validation;

import java.util.Arrays;
import java.util.List;

public class ChequeValidator {

    private final static List<EstadoCheque> estadosInvalidos = Arrays.asList(EstadoCheque.CORREGIDO,
            EstadoCheque.OBSERVADO, EstadoCheque.DERIVADO_FILIAL, EstadoCheque.RECHAZADO, EstadoCheque.BALANCEADO,
            EstadoCheque.DIFERIDO_BALANCEADO, EstadoCheque.ELIMINADO_DUP, EstadoCheque.ELIMINADO);

    public static Validation<String, Cheque> validateStatusForUpdating(Cheque cheque) {
        return (estadosInvalidos.contains(cheque.getEstado())) ? Validation.invalid("El estado del cheque ") : Validation.valid(cheque);
    }

}
