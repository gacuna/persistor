package coop.bancocredicoop.guv.persistor.controllers.api;

import coop.bancocredicoop.guv.persistor.services.CorreccionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/api/persistor")
public class PersistorController {

    @Autowired
    private CorreccionService correccionService;

    private static Logger log = LoggerFactory.getLogger(PersistorController.class);

    @PostMapping()
    public Mono<String> save() {
        //TODO Realizar el update correspondiente
        return Mono.just("OK");
    }

}
