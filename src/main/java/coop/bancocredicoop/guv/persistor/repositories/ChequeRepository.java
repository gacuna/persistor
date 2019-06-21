package coop.bancocredicoop.guv.persistor.repositories;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Repository
public interface ChequeRepository extends CrudRepository<Cheque, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cheque c WHERE c.cmc7.numero = :numero " +
            "AND c.estado NOT IN ('RESCATADO', 'ELIMINADO', 'ELIMINADO_DUP') AND c.cmc7.codCuenta NOT IN (88888888888) " +
            "AND c.fechaIngreso BETWEEN :fechaIngreso AND :fechaActual " +
            "AND c.id <> :id")
    Boolean existCMC7MatchingBetweenDatesAndNotCuentaAjusteNotId(@Param("numero") BigInteger numero,
                                                                 @Param("fechaIngreso") Date fechaIngreso,
                                                                 @Param("fechaActual") Date fechaActual,
                                                                 @Param("id") Long id);

}
