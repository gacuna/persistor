package coop.bancocredicoop.guv.persistor.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Moneda {

    @Id
    private Long id;

    @Column(name = "codmoneda")
    private Integer codMoneda;
}
