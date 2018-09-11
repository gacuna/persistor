package coop.bancocredicoop.proyectos.guvpersistor.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Cheque implements Serializable {

    @Id
    private Long id;
    private String activo;

    public Cheque() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public Cheque(Long id){
        this.id = id;
    }
}
