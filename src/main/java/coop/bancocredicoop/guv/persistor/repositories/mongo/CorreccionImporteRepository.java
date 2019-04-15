package coop.bancocredicoop.guv.persistor.repositories.mongo;

import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CorreccionImporteRepository extends ReactiveMongoRepository<Correccion, Long> {

}
