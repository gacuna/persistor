package coop.bancocredicoop.guv.persistor.models.mongo;

import coop.bancocredicoop.guv.persistor.models.CMC7;

import java.math.BigDecimal;
import java.util.Date;

public class CorreccionCMC7 extends Correccion {

    public CorreccionCMC7(Long id, BigDecimal importe, Date fechaDiferida, String cuit, Integer codMoneda, CMC7 cmc7) {
        super(id, importe, fechaDiferida, cuit, codMoneda, cmc7);
    }

}
