package coop.bancocredicoop.guv.persistor.repositories.mongo;

import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionCUIT;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CorreccionCUITRepository extends ReactiveMongoRepository<CorreccionCUIT, Long> {

    Flux<CorreccionCUIT> findFirst20ByCreatedAtNull();
}
