package coop.bancocredicoop.guv.persistor.repositories;

import coop.bancocredicoop.guv.persistor.models.Cheque;
import coop.bancocredicoop.guv.persistor.models.Deposito;
import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionCMC7;
import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionFecha;
import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionImporte;
import coop.bancocredicoop.guv.persistor.models.mongo.CorreccionCUIT;
import coop.bancocredicoop.guv.persistor.models.EstadoCheque;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChequeRepository extends CrudRepository<Cheque, Long> {

    @Query(value =
            "SELECT new coop.bancocredicoop.guv.persistor.models.mongo.CorreccionImporte(c.id, c.importe, " +
                    "c.fechaDiferida, c.cuit, m.codMoneda, c.cmc7) " +
            "FROM Cheque c " +
            "JOIN c.deposito d " +
            "JOIN c.moneda m " +
            "WHERE c.activo = 1 " +
            "AND c.estado = :estado " +
            "AND (c.importe = 0 OR c.importe IS NULL) " +
            "AND c.id NOT IN (:ids) " +
            "AND c.id NOT IN (" +
                    "SELECT c2.id " +
                    "FROM Cheque c2 " +
                    "JOIN c2.deposito d2 " +
                    "WHERE d2.estado = :estadoDeposito " +
                    "AND :observacion member of c2.observaciones) " +
            "ORDER BY d.prioridadForzada ASC ")
    List<CorreccionImporte> findCorreccionImporte(@Param("estado") EstadoCheque estado,
                                                  @Param("ids") List<Long> idList,
                                                  @Param("estadoDeposito") Deposito.Estado estadoDeposito,
                                                  @Param("observacion") Cheque.Observacion observacion,
                                                  Pageable request);

    @Query(value =
            "SELECT new coop.bancocredicoop.guv.persistor.models.mongo.CorreccionCUIT(c.id, c.importe, " +
                    "c.fechaDiferida, c.cuit, m.codMoneda, c.cmc7) " +
                    "FROM Cheque c " +
                    "JOIN c.deposito d " +
                    "JOIN c.moneda m " +
                    "WHERE c.activo = 1 " +
                    "AND c.estado = :estado " +
                    "AND d.tipoOperatoria IN (:tiposOperatoria) " +
                    "AND c.cuit IS NULL " +
                    "AND c.id NOT IN (:ids) " +
                    "AND c.id NOT IN (" +
                    "SELECT c2.id " +
                    "FROM Cheque c2 " +
                    "JOIN c2.deposito d2 " +
                    "WHERE d2.estado = :estadoDeposito " +
                    "AND :observacion member of c2.observaciones) " +
                    "ORDER BY d.prioridadForzada ASC ")
    List<CorreccionCUIT> findCorreccionCUIT(@Param("estado") EstadoCheque estado,
                                            @Param("tiposOperatoria") List<Deposito.TipoOperatoria> tiposOperatoria,
                                            @Param("ids") List<Long> idList,
                                            @Param("estadoDeposito") Deposito.Estado estadoDeposito,
                                            @Param("observacion") Cheque.Observacion observacion,
                                            Pageable request);

    @Query(value =
            "SELECT new coop.bancocredicoop.guv.persistor.models.mongo.CorreccionCMC7(c.id, c.importe, " +
                    "c.fechaDiferida, c.cuit, m.codMoneda, c.cmc7) " +
                    "FROM Cheque c " +
                    "JOIN c.deposito d " +
                    "JOIN c.moneda m " +
                    "WHERE c.activo = 1 " +
                    "AND c.estado = :estado " +
                    "AND c.numero IS NULL " +
                    "AND c.id NOT IN (:ids) " +
                    "AND c.id NOT IN (" +
                    "SELECT c2.id " +
                    "FROM Cheque c2 " +
                    "JOIN c2.deposito d2 " +
                    "WHERE d2.estado = :estadoDeposito " +
                    "AND :observacion member of c2.observaciones) " +
                    "ORDER BY d.prioridadForzada ASC ")
    List<CorreccionCMC7> findCorreccionCMC7(@Param("estado") EstadoCheque estado,
                                            @Param("ids") List<Long> idList,
                                            @Param("estadoDeposito") Deposito.Estado estadoDeposito,
                                            @Param("observacion") Cheque.Observacion observacion,
                                            Pageable request);

    @Query(value =
            "SELECT new coop.bancocredicoop.guv.persistor.models.mongo.CorreccionFecha(c.id, c.importe, " +
                    "c.fechaDiferida, c.cuit, m.codMoneda, c.cmc7) " +
                    "FROM Cheque c " +
                    "JOIN c.deposito d " +
                    "JOIN c.moneda m " +
                    "WHERE c.activo = 1 " +
                    "AND c.estado = :estado " +
                    "AND d.tipoOperatoria IN (:tiposOperatoria) " +
                    "AND (c.fechaIngreso1 IS NULL OR c.fechaIngreso2 IS NULL) " +
                    "AND c.id NOT IN (:ids) " +
                    "AND c.id NOT IN (" +
                    "SELECT c2.id " +
                    "FROM Cheque c2 " +
                    "JOIN c2.deposito d2 " +
                    "WHERE d2.estado = :estadoDeposito " +
                    "AND :observacion member of c2.observaciones) " +
                    "ORDER BY d.prioridadForzada ASC ")
    List<CorreccionFecha> findCorreccionFecha(@Param("estado") EstadoCheque estado,
                                              @Param("tiposOperatoria") List<Deposito.TipoOperatoria> tiposOperatoria,
                                              @Param("ids") List<Long> idList,
                                              @Param("estadoDeposito") Deposito.Estado estadoDeposito,
                                              @Param("observacion") Cheque.Observacion observacion,
                                              Pageable request);


}
