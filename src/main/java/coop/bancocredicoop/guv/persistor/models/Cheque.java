package coop.bancocredicoop.guv.persistor.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Audited
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

    @NotAudited
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

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda moneda;

    @Column(name = "fechaingreso")
    private Date fechaIngreso;

    @Column(name = "fechaingreso1")
    private Date fechaIngreso1;

    @Column(name = "fechaingreso2")
    private Date fechaIngreso2;

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

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
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

    @JsonIgnore
    public Boolean isCorregido() {
        return this.importeCorregido() && this.cmc7Corregido() && this.fechaCorregida() && this.cuitCorregido();
    }

    @JsonIgnore
    public Boolean importeCorregido() {
        return ((this.getImporte() != null) && (this.getImporte().compareTo(BigDecimal.ZERO) > 0)) || this.observaciones.contains(Observacion.IMPORTE);
    }

    @JsonIgnore
    public Boolean cmc7Corregido() {
        return this.cmc7.isCorregido() || isCmc7Observado();
    }

    @JsonIgnore
    private Boolean isCmc7Observado() {
        return this.observaciones.contains(Observacion.CMC7);
    }

    @JsonIgnore
    public Boolean fechaCorregida() {
        boolean flagFechaDiferida = Boolean.TRUE;
        if (!Deposito.TipoOperatoria.VAL_NEG.equals(this.deposito.getTipoOperatoria()) && this.deposito.getTipoOperatoria().isDiferido()) {
            flagFechaDiferida = this.fechaDiferida != null;
        }
        return flagFechaDiferida || this.observaciones.contains(Observacion.FECHA);
    }

    @JsonIgnore
    public Boolean cuitCorregido() {
        boolean flagCuit = Boolean.TRUE;
        if (!Deposito.TipoOperatoria.VAL_NEG.equals(this.deposito.getTipoOperatoria()) && this.deposito.getTipoOperatoria().isDiferido()) {
            flagCuit = this.cuit != null;
        }
        return flagCuit || this.observaciones.contains(Observacion.CUIT);
    }

    @Override
    public String toString() {
        return "Cheque{" +
                "id=" + id +
                ", importe=" + importe +
                ", estado=" + estado +
                ", fechaDiferida=" + fechaDiferida +
                ", cuit=" + cuit +
                ", deposito=" + ObjectUtils.nullSafeToString(deposito) +
                ", moneda=" + moneda +
                ", fechaIngreso=" + fechaIngreso +
                ", fechaIngreso1=" + fechaIngreso1 +
                ", fechaIngreso2=" + fechaIngreso2 +
                ", truncado=" + truncado +
                ", cmc7=" + ObjectUtils.nullSafeToString(cmc7)  +
                '}';
    }

    public Cheque initialize() {
        Hibernate.initialize((this.getObservaciones()));
        Hibernate.initialize((this.getMoneda()));
        Hibernate.initialize((this.getDeposito()));
        //Hibernate.initialize((this.getRechazos()));
        //Hibernate.initialize((this.getBancoDepositante()));
        return this;
    }

}
