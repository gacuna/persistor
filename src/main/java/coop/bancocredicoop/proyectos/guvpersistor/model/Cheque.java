package coop.bancocredicoop.proyectos.guvpersistor.model;

import java.io.Serializable;

public class Cheque implements Jsoneable, Serializable {

    private Long id;
    private String cmc7;

    public Cheque() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCmc7() {
        return cmc7;
    }

    public void setCmc7(String cmc7) {
        this.cmc7 = cmc7;
    }
}
