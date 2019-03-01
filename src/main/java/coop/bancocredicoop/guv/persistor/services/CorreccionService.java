package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.repositories.ChequeRepository;
import coop.bancocredicoop.guv.persistor.utils.GuvConfigEnum;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;

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

    private BigDecimal importeTruncamiento;

    @PostConstruct
    private void loadParameters() {
        this.importeTruncamiento = this.guvConfigService.getProperty(GuvConfigEnum.IMPORTE_TRUCAMIENTO, BigDecimal.class);
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
            HttpEntity<Long> request = new HttpEntity<>(correccion.getId());
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("GUV-AUTH-TOKEN", token);

            ResponseEntity<String> response = restTemplate.postForEntity(guvUrl + verificacionDepositoEndpoint, request, String.class);
            return response.getStatusCodeValue();
        });
    }

    public Correccion chequearTruncamientoAndApply(String type, Function1<Correccion, Correccion> f, Correccion correccion) {
        return Match(type).of(
                Case($("importe"), f.apply(correccion)),
                Case($(), correccion)
        );
    }

    public Function1<Correccion, Correccion> truncarSiSuperaImporteTruncamiento = (Correccion correccion) -> {
        correccion.setTruncado(this.importeTruncamiento.compareTo(correccion.getImporte()) > 0);
        return correccion;
    };

}