package coop.bancocredicoop.guv.persistor.models;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Audited
@Entity
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "bancoid_seq")
    @SequenceGenerator(name = "bancoid_seq", sequenceName = "BANCOID_SEQ")
    private Long id;

    @Column(name = "codBanco")
    private Integer codBanco;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "emailInstitucional")
    private String emailInstitucional;

    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<String> emailsResponsables;

    public Banco() {
        this.emailsResponsables = new HashSet<>();
    }

}
