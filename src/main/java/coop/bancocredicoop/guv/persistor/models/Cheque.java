package coop.bancocredicoop.guv.persistor.models;

import coop.bancocredicoop.guv.persistor.models.mongo.Correccion;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

@Entity
public class Cheque implements Serializable {

    public enum Observacion {
        CMC7,
        IMPORTE,
        FECHA,
        CUIT
    }

    @Id
    private Long id;

    @Column(name = "activo")
    private boolean activo;

    @Column(name = "importe")
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCheque estado;

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

    @Column(name = "truncado")
    private Boolean truncado;

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

    public CMC7 getCmc7() {
        return cmc7;
    }

    public void setCmc7(CMC7 cmc7) {
        this.cmc7 = cmc7;
    }

    public Boolean getTruncado() {
        return truncado;
    }

    public void setTruncado(Boolean truncado) {
        this.truncado = truncado;
    }

    public void addObservacion(Observacion observacion) {
        this.observaciones.add(observacion);
    }

    public static Cheque of(Correccion correccion) {
        Cheque cheque = new Cheque();
        cheque.setId(correccion.getId());
        cheque.setImporte(correccion.getImporte());
        cheque.setFechaDiferida(correccion.getFechaDiferida());
        cheque.setCuit(correccion.getCuit());
        cheque.setDeposito(correccion.getDeposito());
        cheque.setFechaIngreso1(correccion.getFechaIngreso1());
        cheque.setFechaIngreso2(correccion.getFechaIngreso2());
        cheque.setTruncado(correccion.getTruncado());
        cheque.setCmc7(correccion.getCmc7());

        return cheque;
    }

    public Boolean isCorregido() {
        return this.importeCorregido() && this.cmc7Corregido() && this.fechaCorregida() && this.cuitCorregido();
    }

    public Boolean importeCorregido() {
        return ((this.getImporte() != null) && (this.getImporte().compareTo(BigDecimal.ZERO) > 0)) || this.observaciones.contains(Observacion.IMPORTE);
    }

    public Boolean cmc7Corregido() {
        return this.cmc7.isCorregido() || isCmc7Observado();
    }

    private Boolean isCmc7Observado() {
        return this.observaciones.contains(Observacion.CMC7);
    }

    public Boolean fechaCorregida() {
        boolean flagFechaDiferida = Boolean.TRUE;
        if (!Deposito.TipoOperatoria.VAL_NEG.equals(this.deposito.getTipoOperatoria()) && this.deposito.getTipoOperatoria().isDiferido()) {
            flagFechaDiferida = this.fechaDiferida != null;
        }
        return flagFechaDiferida || this.observaciones.contains(Observacion.FECHA);
    }

    public Boolean cuitCorregido() {
        boolean flagCuit = Boolean.TRUE;
        if (!Deposito.TipoOperatoria.VAL_NEG.equals(this.deposito.getTipoOperatoria()) && this.deposito.getTipoOperatoria().isDiferido()) {
            flagCuit = this.cuit != null;
        }
        return flagCuit || this.observaciones.contains(Observacion.CUIT);
    }

}
