package coop.bancocredicoop.guv.persistor.models;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Localidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "localidadid_seq")
    @SequenceGenerator(name = "localidadid_seq", sequenceName = "LOCALIDADID_SEQ")
    private Long id;

    private String nombre;

    private Integer contador;

    private String nombreProvincia;

    private Integer puntoIntercambio;

    private Integer postalBancario;

    private Integer banco;

    private Integer filial;

    private String tipoPlaza;

    private Date fechaActualizacion;

    private Boolean flagManual;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoPlaza() {
        return tipoPlaza;
    }

    public void setTipoPlaza(String tipoPlaza) {
        this.tipoPlaza = tipoPlaza;
    }

    public String getNombreProvincia() {
        return nombreProvincia;
    }

    public void setNombreProvincia(String nombreProvincia) {
        this.nombreProvincia = nombreProvincia;
    }

    public Integer getPuntoIntercambio() {
        return puntoIntercambio;
    }

    public void setPuntoIntercambio(Integer puntoIntercambio) {
        this.puntoIntercambio = puntoIntercambio;
    }

    public Integer getPostalBancario() {
        return postalBancario;
    }

    public void setPostalBancario(Integer postalBancario) {
        this.postalBancario = postalBancario;
    }

    public Integer getBanco() {
        return banco;
    }

    public void setBanco(Integer banco) {
        this.banco = banco;
    }

    public Integer getContador() {
        return contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
    }

    public Integer getFilial() {
        return filial;
    }

    public void setFilial(Integer filial) {
        this.filial = filial;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Boolean getFlagManual() {
        return flagManual;
    }

    public void setFlagManual(Boolean flagManual) {
        this.flagManual = flagManual;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
