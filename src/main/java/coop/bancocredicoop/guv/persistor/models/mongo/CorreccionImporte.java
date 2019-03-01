package coop.bancocredicoop.guv.persistor.models.mongo;

import coop.bancocredicoop.guv.persistor.models.CMC7;
import coop.bancocredicoop.guv.persistor.models.Deposito;

import java.math.BigDecimal;
import java.util.Date;

public class CorreccionImporte extends Correccion {

    public CorreccionImporte(Long id, BigDecimal importe, Date fechaDiferida, Date fechaIngreso1, Date fechaIngreso2,
                             String cuit, Integer codMoneda, CMC7 cmc7, Deposito deposito) {
        super(id, importe, fechaDiferida, fechaIngreso1, fechaIngreso2, cuit, codMoneda, cmc7, deposito);
    }

}
