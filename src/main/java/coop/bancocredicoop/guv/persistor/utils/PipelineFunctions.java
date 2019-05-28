package coop.bancocredicoop.guv.persistor.utils;

import coop.bancocredicoop.guv.persistor.models.CMC7;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.Deposito;
import coop.bancocredicoop.guv.persistor.models.EstadoCheque;
import coop.bancocredicoop.guv.persistor.services.ChequeService;
import coop.bancocredicoop.guv.persistor.services.FeriadoService;
import coop.bancocredicoop.guv.persistor.services.GuvConfigService;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

@Component
public class PipelineFunctions {

    public final static String CREDICOOP_CUIT = "30571421352";

    private Optional<BigDecimal> importeTruncamiento;

    @Autowired
    private GuvConfigService guvConfigService;

    @Autowired
    private FeriadoService feriadoService;

    @Autowired
    private ChequeService chequeService;

    private static Logger LOGGER = LoggerFactory.getLogger(PipelineFunctions.class);

    @PostConstruct
    private void loadParameters() {
        this.importeTruncamiento = Optional.ofNullable(this.guvConfigService.getProperty(GuvConfigEnum.IMPORTE_TRUCAMIENTO, BigDecimal.class));
    }

    /**
     * Funcion para setear el importe y calcular si un cheque debe ser marcado como truncado.
     */
    public Function2<Cheque, Cheque, Cheque> setImporteAndTruncado = (correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de actualización de Importe y Truncamiento del cheque con id {}", cheque.getId());
        boolean truncado = this.importeTruncamiento.orElse(BigDecimal.ZERO).compareTo(correccion.getImporte()) > 0;
        cheque.setTruncado(truncado);
        cheque.setImporte(correccion.getImporte());
        LOGGER.info("Actualizando Importe {} / Truncado {} del cheque con id {}", correccion.getImporte(), truncado, cheque.getId());
        return cheque;
    };

    /**
     * Funcion para setear el CMC7 de un cheque y ademas remueve alguna observacion de este tipo si es que tuviere.
     */
    public Function2<Cheque, Cheque, Cheque> setCMC7 = (correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de actualización de Cmc7 del cheque con id {}", cheque.getId());
        CMC7 cmc7 = fixCMC7Fields(correccion.getCmc7())
                .onFailure((ex) -> LOGGER.error("Error al fixear el CMC7 {} del cheque con id {}", correccion.getCmc7().toString(), cheque.getId()))
                .getOrElse(correccion.getCmc7());
        cmc7.setNumero(new BigInteger(cmc7.toString()));
        cheque.setCmc7(cmc7);
        LOGGER.info("Actualizando CMC7 {} del cheque con id {}", cmc7.toString(), cheque.getId());

        if (!cheque.getObservaciones().isEmpty()) {
            LOGGER.info("Removiendo las observaciones de tipo CMC7 del cheque con id {}", cheque.getId());
            cheque.getObservaciones().remove(Cheque.Observacion.CMC7);
        }

        return cheque;
    };

    /**
     * Funcion para calcular que fecha del cheque debe setearse.
     */
    public Function2<Cheque, Cheque, Cheque> setFecha = (correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de actualización de Fecha del cheque con id {}", cheque.getId());
        if (cheque.getFechaIngreso1() == null) {
            LOGGER.info("Actualizando FechaIngreso1 {} del cheque con id {}", correccion.getFechaDiferida(), cheque.getId());
            cheque.setFechaIngreso1(correccion.getFechaDiferida());
        } else if (cheque.getFechaIngreso2() == null) {
            LOGGER.info("Actualizando FechaIngreso2 {} del cheque con id {}", correccion.getFechaDiferida(), cheque.getId());
            cheque.setFechaIngreso2(correccion.getFechaDiferida());
        }

        if (cheque.getFechaIngreso1() != null && cheque.getFechaIngreso2() != null) {
            if (DateUtils.isSameDay(cheque.getFechaIngreso1(), cheque.getFechaIngreso2())) {
                LOGGER.info("Actualizando FechaDiferida {} del cheque con id {}", cheque.getFechaIngreso1(), cheque.getId());
                cheque.setFechaDiferida(cheque.getFechaIngreso1());
            } else {
                LOGGER.info("Agregando Observacion de tipo Fecha del cheque con id {}", cheque.getId());
                cheque.addObservacion(Cheque.Observacion.FECHA);
            }
        }
        return cheque;
    };

    /**
     * Funcion para setear el cuit de un cheque.
     */
    public Function2<Cheque, Cheque, Cheque> setCuit = (correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de actualización de Cuit del cheque con id {}", cheque.getId());
        LOGGER.info("Actualizando CUIT {} del cheque con id {}", correccion.getCuit(), cheque.getId());
        cheque.setCuit(correccion.getCuit());
        return cheque;
    };

    /**
     * Funcion que permite calcular el nuevo estado del cheque.
     */
    public Function2<Cheque, Cheque, Cheque> setStatus = (correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de actualización de Estado del cheque con id {}", cheque.getId());
        if (cheque.isCorregido() && cheque.getObservaciones().isEmpty()) {
            //Cheque corregido y sin observaciones
            cheque.setEstado(EstadoCheque.CORREGIDO);
        } else if (cheque.isCorregido()) {
            // Verifica si el cheque esta duplicado por CMC7
            if (!cheque.getObservaciones().contains(Cheque.Observacion.CMC7)
                    && chequeService.existeCMC7Dulicado(cheque.getCmc7().getNumero())) {
                LOGGER.info("Marcando al cheque con id {} como duplicado por cmc7", cheque.getId());
                cheque.setEstado(EstadoCheque.ELIMINADO_DUP);
            } else {
                //Cheque corregido pero con observaciones.
                cheque.setEstado(EstadoCheque.OBSERVADO);
            }
        }
        LOGGER.info("Actualizando Estado {} del cheque con id {}", cheque.getEstado(), cheque.getId());
        return cheque;
    };

    /**
     * Funcion que permite actuaizar la fecha diferida y el cuit del cheque para el
     * tipo de operatoria VALORES NEGOCIADOS.
     */
    public Function2<Cheque, Cheque, Cheque> setFechaDiferidaAndCuit = (correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de Valores Negociados del cheque con id {}", cheque.getId());
        if (Deposito.TipoOperatoria.VAL_NEG.equals(cheque.getDeposito().getTipoOperatoria())) {
            if (cheque.getFechaDiferida() == null) {
                Date proxHabil = feriadoService.calcularProximoDiaHabil(new Date(), 1);
                cheque.setFechaDiferida(proxHabil);
                LOGGER.info("Actualizando fecha diferida {} al cheque con id {}", proxHabil, CREDICOOP_CUIT, cheque.getId());
            }
            if (StringUtils.trimToEmpty(cheque.getCuit()).length() == 0) {
                cheque.setCuit(CREDICOOP_CUIT);
                LOGGER.info("Actualizando cuit {} (CREDICOOP) al cheque con id {}", CREDICOOP_CUIT, cheque.getId());
            }
        }
        return cheque;
    };

    public Function3<Cheque.Observacion, Cheque, Cheque, Cheque> setObservacion = ((observacion, correccion, cheque) -> {
        LOGGER.info("Ejecutando pipeline de Observaciones del cheque con id {}", cheque.getId());
        if (!cheque.getObservaciones().contains(observacion)) {
            LOGGER.info("Agregando observacion de tipo {} del cheque con id {}", observacion.name(), cheque.getId());
            cheque.addObservacion(observacion);
        }
        return cheque;
    });

    public Function2<Cheque, Cheque, Cheque> asd = (correccion, cheque) ->
            this.setImporteAndTruncado.curried().apply(cheque)
            .andThen(this.setCMC7.curried().apply(cheque))
            .andThen(this.setFecha.curried().apply(cheque))
            .andThen(this.setCuit.curried().apply(cheque)).apply(cheque);

    private Try<CMC7> fixCMC7Fields(CMC7 cmc7) {
        return Try.of(() -> {
            cmc7.setCodBanco(StringUtils.leftPad(cmc7.getCodBanco(), 3, "0"));
            cmc7.setCodFilial(StringUtils.leftPad(cmc7.getCodFilial(), 3, "0"));
            cmc7.setCodPostal(StringUtils.leftPad(cmc7.getCodPostal(), 4, "0"));
            cmc7.setCodCheque(StringUtils.leftPad(cmc7.getCodCheque(), 8, "0"));
            cmc7.setCodCuenta(StringUtils.leftPad(cmc7.getCodCuenta(), 11, "0"));
            return cmc7;
        });
    }

}
