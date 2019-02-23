package coop.bancocredicoop.guv.persistor.repositories.mongo;

import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionCMC7;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CorreccionCMC7Repository extends ReactiveMongoRepository<CorreccionCMC7, Long> {

    Flux<CorreccionCMC7> findFirst20ByCreatedAtNull();
}
