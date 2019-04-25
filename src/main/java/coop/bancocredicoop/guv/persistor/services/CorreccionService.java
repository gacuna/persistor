package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.exceptions.InvalidEntityStateException;
import coop.bancocredicoop.guv.persistor.models.CMC7;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.Deposito;
import coop.bancocredicoop.guv.persistor.models.EstadoCheque;
import coop.bancocredicoop.guv.persistor.repositories.ChequeRepository;
import coop.bancocredicoop.guv.persistor.utils.ChequeValidator;
import coop.bancocredicoop.guv.persistor.utils.GuvConfigEnum;
import io.vavr.Function2;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import io.vavr.control.Validation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class CorreccionService {

    public final static String GUV_AUTH_TOKEN = "GUV_AUTH_TOKEN";

    public final static String CREDICOOP_CUIT = "30571421352";

    @Value(value = "${guv-web.url}")
    private String guvUrl;

    @Value(value = "${guv-web.correccion.save.endpoint}")
    private String saveCorreccionEndpoint;

    @Autowired
    private ChequeRepository chequeRepository;

    @Autowired
    private ChequeService chequeService;

    @Autowired
    private FeriadoService feriadoService;

    @Autowired
    private GuvConfigService guvConfigService;

    private Optional<BigDecimal> importeTruncamiento;

    private static Logger LOGGER = LoggerFactory.getLogger(CorreccionService.class);

    @PostConstruct
    private void loadParameters() {
        this.importeTruncamiento = Optional.ofNullable(this.guvConfigService.getProperty(GuvConfigEnum.IMPORTE_TRUCAMIENTO, BigDecimal.class));
    }

    /**
     *
     * @param correccion
     * @return
     */
    public Future<Cheque> update(Function2<Correccion, Cheque, Cheque> decorator, Correccion correccion) {
        return Future.of(() -> {
            LOGGER.info("Cheque con id {} recibido para ser persistido en la base de datos", correccion.getId());
            final Optional<Cheque> chequeOpt = this.chequeRepository.findById(correccion.getId());
            final Cheque cheque = chequeOpt.orElseThrow(EntityNotFoundException::new);
            Validation<String, Cheque> validation = ChequeValidator.validateStatusForUpdating(cheque);
            if (validation.isInvalid()) {
                LOGGER.error("Cheque con id {} no puede ser actualizado dado que su estado en la base de datos es {}", cheque.getId(), cheque.getEstado());
                throw new InvalidEntityStateException("Estado del cheque inconsistente");
            }
            final Cheque chequeDecorated = decorator.apply(correccion, cheque);
            LOGGER.info("Persistiendo cheque con id {} y estado {}", chequeDecorated.getId(), chequeDecorated.getEstado());
            return this.chequeRepository.save(chequeDecorated);
        });
    }

    /**
     * Envia un mensaje de actualizacion de cheque al backend de GUV, utilizando el verbo HTTP POST.
     *
     * @param cheque entidad a actualizar
     * @param token guv access token
     * @return http status code
     */
    public Try<Integer> saveCorreccionBackgroundPost(Cheque cheque, String token) {
        return Try.of(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(CorreccionService.GUV_AUTH_TOKEN, token);

            HttpEntity<Cheque> request = new HttpEntity<Cheque>(cheque, headers);
            RestTemplate restTemplate = new RestTemplate();

            String url = guvUrl + saveCorreccionEndpoint;

            ResponseEntity<Cheque> response = restTemplate.postForEntity(url, request, Cheque.class);
            return response.getStatusCodeValue();
        });
    }

    /**
     * Funcion para setear el importe y calcular si un cheque debe ser marcado como truncado.
     */
    public Function2<Correccion, Cheque, Cheque> setImporteAndTruncado = (correccion, cheque) -> {
        cheque.setTruncado(this.importeTruncamiento.orElse(BigDecimal.ZERO).compareTo(correccion.getImporte()) > 0);
        cheque.setImporte(correccion.getImporte());
        return cheque;
    };

    /**
     * Funcion para setear el CMC7 de un cheque y ademas remueve alguna observacion de este tipo si es que tuviere.
     */
    public Function2<Correccion, Cheque, Cheque> setCMC7 = (correccion, cheque) -> {
        CMC7 cmc7 = fixCMC7Fields(correccion.getCmc7());
        cmc7.setNumero(new BigInteger(cmc7.toString()));
        cheque.setCmc7(cmc7);

        if (!cheque.getObservaciones().isEmpty()) {
            cheque.getObservaciones().remove(Cheque.Observacion.CMC7);
        }

        return cheque;
    };

    /**
     * Funcion para calcular que fecha del cheque debe setearse.
     */
    public Function2<Correccion, Cheque, Cheque> setFecha = (correccion, cheque) -> {
        if (cheque.getFechaIngreso1() == null) {
            cheque.setFechaIngreso1(cheque.getFechaDiferida());
        } else if (cheque.getFechaIngreso2() == null) {
            cheque.setFechaIngreso2(cheque.getFechaDiferida());
        }

        if (cheque.getFechaIngreso1() != null && cheque.getFechaIngreso2() != null) {
            if (DateUtils.isSameDay(cheque.getFechaIngreso1(), cheque.getFechaIngreso2())) {
                cheque.setFechaDiferida(cheque.getFechaIngreso1());
            } else {
                cheque.addObservacion(Cheque.Observacion.FECHA);
            }
        }
        return cheque;
    };

    /**
     * Funcion para setear el cuit de un cheque.
     */
    public Function2<Correccion, Cheque, Cheque> setCuit = (correccion, cheque) -> {
        cheque.setCuit(correccion.getCuit());
        return cheque;
    };

    /**
     * Funcion que permite calcular el nuevo estado del cheque.
     */
    public Function2<Correccion, Cheque, Cheque> setStatus = (correccion, cheque) -> {
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
        return cheque;
    };

    /**
     * Funcion que permite actuaizar la fecha diferida y el cuit del cheque para el
     * tipo de operatoria VALORES NEGOCIADOS.
     */
    public Function2<Correccion, Cheque, Cheque> setFechaDiferidaAndCuit = (correccion, cheque) -> {
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

    public Function2<Correccion, Cheque, Cheque> setObservacion = ((correccion, cheque) -> {
        if (!cheque.getObservaciones().contains(correccion.getTipoObservacion())) {
            cheque.addObservacion(correccion.getTipoObservacion());
        }
        return cheque;
    });

    private CMC7 fixCMC7Fields(CMC7 cmc7){
        cmc7.setCodBanco(StringUtils.leftPad(cmc7.getCodBanco(), 3, "0"));
        cmc7.setCodFilial(StringUtils.leftPad(cmc7.getCodFilial(), 3, "0"));
        cmc7.setCodPostal(StringUtils.leftPad(cmc7.getCodPostal(), 4, "0"));
        cmc7.setCodCheque(StringUtils.leftPad(cmc7.getCodCheque(), 8, "0"));
        cmc7.setCodCuenta(StringUtils.leftPad(cmc7.getCodCuenta(), 11, "0"));
        return cmc7;
    }

}