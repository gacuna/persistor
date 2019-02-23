package coop.bancocredicoop.guv.persistor.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

@Entity
public class Cheque implements Serializable {

    @Id
    private Long id;

    @Column(name = "activo")
    private boolean activo;

    @Column(name = "importe")
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCheque estado;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "es_AR", timezone = "America/Argentina/Buenos_Aires")
    @Column(name = "fechadiferida")
    private Date fechaDiferida;

    @Column(name = "cuit")
    private String cuit;

    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda moneda;

    @Column(name = "fechaingreso1")
    private Date fechaIngreso1;

    @Column(name = "fechaingreso2")
    private Date fechaIngreso2;

    @Column(name = "numero")
    private BigInteger numero;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Observacion> observaciones;

    @Embedded
    private CMC7 cmc7;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public EstadoCheque getEstado() {
        return estado;
    }

    public void setEstado(EstadoCheque estado) {
        this.estado = estado;
    }

    public Date getFechaDiferida() {
        return fechaDiferida;
    }

    public void setFechaDiferida(Date fechaDiferida) {
        this.fechaDiferida = fechaDiferida;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public Date getFechaIngreso1() {
        return fechaIngreso1;
    }

    public void setFechaIngreso1(Date fechaIngreso1) {
        this.fechaIngreso1 = fechaIngreso1;
    }

    public Date getFechaIngreso2() {
        return fechaIngreso2;
    }

    public void setFechaIngreso2(Date fechaIngreso2) {
        this.fechaIngreso2 = fechaIngreso2;
    }

    public BigInteger getNumero() {
        return numero;
    }

    public void setNumero(BigInteger numero) {
        this.numero = numero;
    }

    public Set<Observacion> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Set<Observacion> observaciones) {
        this.observaciones = observaciones;
    }

    public enum Observacion {
        CMC7,
        IMPORTE,
        FECHA,
        CUIT
    }

}
