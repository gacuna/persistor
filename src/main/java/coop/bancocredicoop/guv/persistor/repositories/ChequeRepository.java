package coop.bancocredicoop.guv.persistor.repositories;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ChequeRepository extends CrudRepository<Cheque, Long> {

    //TODO VER SI VALE LA PENA HACER UNA QUERY NATIVA POR CADA TIPO DE CORRECCION
    //@Query(value = "UPDATE cheque SET importe = :importe, truncado = :truncado WHERE id = :id", nativeQuery = true)
    //public void updateImporte(@Param("id") Long id, @Param("importe") BigDecimal importe, @Param("truncado") Boolean truncado);
}
