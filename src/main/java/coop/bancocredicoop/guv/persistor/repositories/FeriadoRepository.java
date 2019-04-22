package coop.bancocredicoop.guv.persistor.repositories;

import coop.bancocredicoop.guv.persistor.models.Feriado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface FeriadoRepository extends CrudRepository<Feriado, Long> {

    Feriado findByFechaAndNacionalTrue(Date fecha);

}
