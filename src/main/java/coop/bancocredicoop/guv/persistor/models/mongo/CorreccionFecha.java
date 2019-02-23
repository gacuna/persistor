package coop.bancocredicoop.guv.persistor.models.mongo;

import coop.bancocredicoop.guv.persistor.models.CMC7;

import java.math.BigDecimal;
import java.util.Date;

public class CorreccionFecha extends Correccion {

    public CorreccionFecha(Long id, BigDecimal importe, Date fechaDiferida, String cuit, Integer codMoneda, CMC7 cmc7) {
        super(id, importe, fechaDiferida, cuit, codMoneda, cmc7);
    }
}
