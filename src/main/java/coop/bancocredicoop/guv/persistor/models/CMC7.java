package coop.bancocredicoop.guv.persistor.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CMC7 implements Serializable {

    @Column(name = "CODBANCO")
    private String codBanco;

    @Column(name = "CODFILIAL")
    private String codFilial;

    @Column(name = "CODPOSTAL")
    private String codPostal;

    @Column(name = "CODCHEQUE")
    private String codCheque;

    @Column(name = "CODCUENTA")
    private String codCuenta;

    //dv = digito verificador
    @Column(name = "DVBSC")
    private Short dvBsc;
    @Column(name = "DVCH")
    private Short dvCH;
    @Column(name = "DVCUENTA")
    private Short dvCuenta;

    public String getCodCMC7() {
        return codBanco + codFilial + codPostal + codCheque + codCuenta;
    }

    public String getCodBanco() {
        return codBanco;
    }

    public String getCodFilial() {
        return codFilial;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public String getCodCheque() {
        return codCheque;
    }

    public String getCodCuenta() {
        return codCuenta;
    }

    public Short getDvBsc() {
        return dvBsc;
    }

    public Short getDvCH() {
        return dvCH;
    }

    public Short getDvCuenta() {
        return dvCuenta;
    }
}
