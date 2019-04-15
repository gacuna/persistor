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

    @Value(value = "${guv-web.verificacion.deposito.endpoint}")
    private String verificacionDepositoEndpoint;

    @Autowired
    private ChequeRepository repository;

    @Autowired
    private GuvConfigService guvConfigService;

    private Optional<BigDecimal> importeTruncamiento;

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
     * Envia un mensaje de verificacion del estado del deposito al backend de GUV, utilizando el verbo HTTP POST.
     *
     * @param correccion
     * @return
     */
    public Try<Integer> verificacionDepositoBackgroundPost(Correccion correccion, String token) {
        return Try.of(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(CorreccionUtils.GUV_AUTH_TOKEN, token);

            HttpEntity<Long> request = new HttpEntity<>(correccion.getId(), headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(guvUrl + verificacionDepositoEndpoint, request, String.class);
            return response.getStatusCodeValue();
        });
    }

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
        return Try.of(() -> correccion);
    };

    /**
     * No realiza ninguna validacion y devuelve la misma correccion recibida por parametro.
     */
    public Function1<Correccion, Try<Correccion>> defaultValidation = (Correccion correccion) -> {
        return Try.of(() -> correccion);
    };

}