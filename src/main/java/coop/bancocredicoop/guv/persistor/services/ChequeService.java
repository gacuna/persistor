package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.repositories.ChequeRepository;
import coop.bancocredicoop.guv.persistor.utils.GuvConfigEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Date;

@Service
public class ChequeService {

    @Autowired
    private ChequeRepository chequeRepository;

    @Autowired
    private FeriadoService feriadoService;

    @Autowired
    private GuvConfigService guvConfigService;

    private Integer cantidadDias;

    private static Logger LOGGER = LoggerFactory.getLogger(CorreccionService.class);

    @PostConstruct
    private void loadParameters() {
        this.cantidadDias = this.guvConfigService.getProperty(GuvConfigEnum.DIAS_VALIDACION_CMC7_DUPLICADOS, Integer.class);
    }

    public Boolean existeCMC7ByNumeroEntreFechas(BigInteger numero, Date fechaPresentacion, Date fechaActual) {
        return chequeRepository.existCMC7MatchingBetweenDatesAndNotCuentaAjuste(numero, fechaPresentacion, fechaActual);
    }

    public Boolean existeCMC7Dulicado(BigInteger numeroCMC7) {
        Date fechaActual = new Date();
        Date fechaIngreso = feriadoService.calcularProximoDiaHabil(fechaActual, this.cantidadDias, true);
        return existeCMC7ByNumeroEntreFechas(numeroCMC7, fechaIngreso, fechaActual);
    }

}
