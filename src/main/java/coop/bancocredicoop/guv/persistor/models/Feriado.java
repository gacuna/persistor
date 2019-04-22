package coop.bancocredicoop.guv.persistor.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
public class Feriado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "feriadoid_seq")
    @SequenceGenerator(name = "feriadoid_seq", sequenceName = "FERIADOID_SEQ")
    private Long id;

    private String descripcion;

    private Date fecha;

    private boolean nacional;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "FERIADO_LOCALIDAD", joinColumns = @JoinColumn(name = "FERIADO_ID", referencedColumnName = "ID"))
    private Collection<Localidad> localidades;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isNacional() {
        return nacional;
    }

    public void setNacional(boolean nacional) {
        this.nacional = nacional;
    }

    public Collection<Localidad> getLocalidades() {
        return localidades;
    }

    public void setLocalidades(Collection<Localidad> localidades) {
        this.localidades = localidades;
    }

}
