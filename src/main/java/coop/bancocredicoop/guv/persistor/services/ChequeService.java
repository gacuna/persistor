package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.repositories.ChequeRepository;
import coop.bancocredicoop.guv.persistor.utils.GuvConfigEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

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

    public Boolean existeCMC7ByNumeroEntreFechas(BigInteger numero, Date fechaPresentacion, Date fechaActual, Long id) {
        return chequeRepository.existCMC7MatchingBetweenDatesAndNotCuentaAjusteNotId(numero, fechaPresentacion, fechaActual, id);
    }

    public Boolean existeCMC7Dulicado(BigInteger numeroCMC7, Long id) {
        Date fechaActual = new Date();
        Date fechaIngreso = feriadoService.calcularProximoDiaHabil(fechaActual, this.cantidadDias, true);
        return existeCMC7ByNumeroEntreFechas(numeroCMC7, fechaIngreso, fechaActual, id);
    }

    @Transactional
    public Optional<Cheque> findById(Long id) {
        return this.chequeRepository.findById(id).map(cheque -> cheque.initialize());
    }

    public Cheque save(Cheque cheque) {
        return this.chequeRepository.save(cheque);
    }

    public void quitarObservaciones(Cheque correccion, Cheque cheque) {
        correccion.getObservaciones().forEach(observacion -> {
            switch (observacion){
                case IMPORTE:{
                    if(correccion.getImporte() != null &&
                            cheque.getImporte() != null &&
                            !correccion.getImporte().equals(cheque.getImporte()))
                        cheque.getObservaciones().remove(Cheque.Observacion.IMPORTE);
                }
                case CMC7:{
                    if(correccion.getCmc7() != null &&
                            cheque.getCmc7() != null &&
                            !correccion.getCmc7().equals(cheque.getCmc7()))
                        cheque.getObservaciones().remove(Cheque.Observacion.CMC7);
                }
                case CUIT:{
                    if(correccion.getCuit() != null &&
                            cheque.getCuit() != null &&
                            !correccion.getCuit().equals(cheque.getCuit()))
                        cheque.getObservaciones().remove(Cheque.Observacion.CUIT);
                }
                case FECHA:{
                    if((correccion.getFechaIngreso1() != null &&
                            cheque.getFechaIngreso1() != null &&
                            !correccion.getCmc7().equals(cheque.getCmc7())) ||
                        (correccion.getFechaIngreso2() != null &&
                            cheque.getFechaIngreso2() != null &&
                            !correccion.getFechaIngreso2().equals(cheque.getFechaIngreso2()))
                    )
                        cheque.getObservaciones().remove(Cheque.Observacion.CMC7);
                }
            }
        });
    }
}
