package coop.bancocredicoop.guv.persistor.models;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;

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

    private BigInteger numero;

    public String getCodCMC7() {
        return codBanco + codFilial + codPostal + codCheque + codCuenta;
    }

    public String getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(String codBanco) {
        this.codBanco = codBanco;
    }

    public String getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(String codFilial) {
        this.codFilial = codFilial;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    public String getCodCheque() {
        return codCheque;
    }

    public void setCodCheque(String codCheque) {
        this.codCheque = codCheque;
    }

    public String getCodCuenta() {
        return codCuenta;
    }

    public void setCodCuenta(String codCuenta) {
        this.codCuenta = codCuenta;
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

    public BigInteger getNumero() {
        return numero;
    }

    public void setNumero(BigInteger numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s", this.codBanco, this.codFilial, this.codPostal, this.codCheque, this.codCuenta);
    }

    public Boolean isCorregido() {
        return !this.toString().toUpperCase().contains("X");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CMC7 cmc7 = (CMC7) o;

        return new EqualsBuilder()
                .append(getCodBanco(), cmc7.getCodBanco())
                .append(getCodFilial(), cmc7.getCodFilial())
                .append(getCodPostal(), cmc7.getCodPostal())
                .append(getCodCheque(), cmc7.getCodCheque())
                .append(getCodCuenta(), cmc7.getCodCuenta())
                .append(getDvBsc(), cmc7.getDvBsc())
                .append(getDvCH(), cmc7.getDvCH())
                .append(getDvCuenta(), cmc7.getDvCuenta())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCodBanco())
                .append(getCodFilial())
                .append(getCodPostal())
                .append(getCodCheque())
                .append(getCodCuenta())
                .append(getDvBsc())
                .append(getDvCH())
                .append(getDvCuenta())
                .toHashCode();
    }

}
