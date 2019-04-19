package coop.bancocredicoop.guv.persistor.repositories;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ChequeRepository extends CrudRepository<Cheque, Long> {
}
