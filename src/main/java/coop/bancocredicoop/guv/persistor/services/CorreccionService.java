package coop.bancocredicoop.guv.persistor.services;

import coop.bancocredicoop.guv.persistor.exceptions.InvalidEntityStateException;
import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.utils.ChequeValidator;
import io.vavr.Function2;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CorreccionService {

    public final static String GUV_AUTH_TOKEN = "GUV-AUTH-TOKEN";

    @Value(value = "${guv-web.url}")
    private String guvUrl;

    @Value(value = "${guv-web.correccion.postSaveProcess.endpoint}")
    private String postSaveProcessEndpoint;

    @Autowired
    private ChequeService chequeService;

    private static Logger LOGGER = LoggerFactory.getLogger(CorreccionService.class);

    /**
     * Metodo que ejecuta un pipeline de validaciones y decoraciones sobre un cheque para finalmente
     * persistirlo en la base de datos.
     *
     * @param correccion
     * @return
     */
    @Transactional
    public Future<Cheque> update(Function2<Cheque, Cheque, Cheque> decorator, Cheque correccion) {
        return Future.of(() -> {
            LOGGER.info("Cheque con id {} recibido para ser persistido en la base de datos", correccion.getId());
            final Optional<Cheque> chequeOpt = this.chequeService.findById(correccion.getId());
            final Cheque chequeBD = chequeOpt.orElseThrow(() -> new EntityNotFoundException("Cheque no encontrado: " + correccion.getId()));
            Validation<String, Cheque> validation = ChequeValidator.validateStatusForUpdating(chequeBD);
            if (validation.isInvalid()) {
                LOGGER.error("Cheque con id {} no puede ser actualizado dado que su estado en la base de datos es {}", chequeBD.getId(), chequeBD.getEstado());
                throw new InvalidEntityStateException(String.format("Estado del cheque {} inconsistente", chequeBD.getId()));
            }
            final Cheque chequeDecorated = decorator.apply(correccion, chequeBD);
            LOGGER.info("Persistiendo cheque con id {} y estado {}", chequeDecorated.getId(), chequeDecorated.getEstado());
            return this.chequeService.save(chequeDecorated);
        });
    }

    /**
     * Envia un mensaje de actualizacion de cheque al backend de GUV, utilizando el verbo HTTP POST.
     *
     * @param cheque entidad a actualizar
     * @param token guv access token
     * @return http status code
     */
    public Try<Integer> postSaveBackgroundPost(Cheque cheque, String token) {
        return Try.of(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(CorreccionService.GUV_AUTH_TOKEN, token);

            HttpEntity<Cheque> request = new HttpEntity<Cheque>(cheque, headers);
            RestTemplate restTemplate = new RestTemplate();

            String url = guvUrl + postSaveProcessEndpoint;

            ResponseEntity<Cheque> response = restTemplate.postForEntity(url, request, Cheque.class);
            return response.getStatusCodeValue();
        });
    }

}