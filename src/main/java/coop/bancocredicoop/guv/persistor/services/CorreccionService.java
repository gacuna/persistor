package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.TipoCorreccionEnum;
import coop.bancocredicoop.guv.persistor.repositories.ChequeRepository;
import coop.bancocredicoop.guv.persistor.utils.CorreccionUtils;
import coop.bancocredicoop.guv.persistor.utils.GuvConfigEnum;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

@Service
public class CorreccionService {

    @Value(value = "${guv-web.url}")
    private String guvUrl;

    @Value(value = "${guv-web.correccion.observar.endpoint}")
    private String observarChequeEndpoint;

    @Value(value = "${guv-web.correccion.save.endpoint}")
    private String saveCorreccionEndpoint;

    @Autowired
    private ChequeRepository repository;

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
    public Future<Cheque> update(Function2<Correccion, Cheque, Cheque> f, Correccion correccion) {
        //TODO Mejorar con un pattern matching y loguear si no hay un cheque con ese id
        return Future.of(() -> this.repository.findById(correccion.getId()))
              .map(cheque -> f.apply(correccion, cheque.get()))
              .map(cheque -> this.repository.save(cheque));
    }

    /**
     * Envia un mensaje de observacion de cheque al backend de GUV, utilizando el verbo HTTP POST.
     *
     * @param cheque
     * @param observacion
     * @param token
     * @return http status code
     */
    public Try<Integer> observarChequeBackgroundPost(Cheque cheque, Cheque.Observacion observacion, String token) {
        return Try.of(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(CorreccionUtils.GUV_AUTH_TOKEN, token);

            HttpEntity<Cheque> request = new HttpEntity<>(cheque, headers);
            RestTemplate restTemplate = new RestTemplate();

            String url = guvUrl + observarChequeEndpoint + observacion.name();

            ResponseEntity<Cheque> response = restTemplate.postForEntity(url, request, Cheque.class);
            return response.getStatusCodeValue();
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
            headers.set(CorreccionUtils.GUV_AUTH_TOKEN, token);

            HttpEntity<Cheque> request = new HttpEntity<Cheque>(cheque, headers);
            RestTemplate restTemplate = new RestTemplate();

            String url = guvUrl + saveCorreccionEndpoint;

            ResponseEntity<Cheque> response = restTemplate.postForEntity(url, request, Cheque.class);
            return response.getStatusCodeValue();
        });
    }

    /**
     *
     * @param type tipo de correccion a validar
     * @param f validacion a ejecutar
     * @param correccion entidad a validar
     * @return
     */
    public Try<Correccion> validateAndApply(TipoCorreccionEnum type, Function1<Correccion, Try<Correccion>> f, Correccion correccion) {
        return Match(type).of(
                Case($(TipoCorreccionEnum.IMPORTE), f.apply(correccion)),
                Case($(TipoCorreccionEnum.CMC7), f.apply(correccion)),
                Case($(), f.apply(correccion))
        );
    }

    /**
     * Valida el importe de truncamiento, de ser asi marca el cheque como truncado.
     */
    public Function1<Correccion, Try<Correccion>> truncarSiSuperaImporteTruncamiento = (Correccion correccion) -> {
        correccion.setTruncado(this.importeTruncamiento.orElse(BigDecimal.ZERO).compareTo(correccion.getImporte()) > 0);
        return Try.of(() -> correccion);
    };

    /**
     * Chequea que no exceda el limite de reintentos, caso contrario devuelve un error.
     */
    public Function1<Correccion, Try<Correccion>> superaReintentosValidos = (Correccion correccion) -> {
        //TODO Ver como manejar los reintentos... podria ser en una cache
        //Si hay error enviar un failure con una exception adentro
        //Si supera los reintentos tiene que poner una observacion en el cheque
        return Try.of(() -> {
            Try<Integer> backgroundPost = observarChequeBackgroundPost(Cheque.of(correccion), Cheque.Observacion.CMC7, "");
            backgroundPost.onFailure(ex -> LOGGER.error("Hubo un error inesperado al observar el cheque con id {}, detalle: {}", correccion.getId(), ex.getMessage()));
            backgroundPost.onSuccess(status -> LOGGER.info("Se realizo correctamente la observacion del cheque con id {}", correccion.getId()));
            return correccion;
        });
    };

    /**
     * No realiza ninguna validacion y devuelve la misma correccion recibida por parametro.
     */
    public Function1<Correccion, Try<Correccion>> defaultValidation = (Correccion correccion) -> {
        return Try.of(() -> correccion);
    };

}