package coop.bancocredicoop.guv.persistor.models.mongo;

import coop.bancocredicoop.guv.persistor.models.CMC7;
import coop.bancocredicoop.guv.persistor.models.Deposito;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Document
public class Correccion implements Serializable {

    protected Long id;
    protected BigDecimal importe;
    protected Date fechaDiferida;
    protected String cuit;
    protected Integer codMoneda;
    protected CMC7 cmc7;
    protected Boolean truncado;
    protected Deposito deposito;

    public Correccion() {}

    public Correccion(Long id, BigDecimal importe, Date fechaDiferida, String cuit, Integer codMoneda,
                      CMC7 cmc7, Deposito deposito) {
        this.id = id;
        this.importe = importe;
        this.fechaDiferida = fechaDiferida;
        this.cuit = cuit;
        this.codMoneda = codMoneda;
        this.cmc7 = cmc7;
        this.deposito = deposito;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public Date getFechaDiferida() {
        return fechaDiferida;
    }

    public String getCuit() {
        return cuit;
    }

    public Integer getCodMoneda() {
        return codMoneda;
    }

    public CMC7 getCmc7() {
        return cmc7;
    }

    public Boolean getTruncado() {
        return truncado;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    @Override
    public String toString() {
        return "Correccion{" +
                "id=" + id +
                ", importe=" + importe +
                ", fechaDiferida=" + fechaDiferida +
                ", cuit='" + cuit + '\'' +
                ", codMoneda=" + codMoneda +
                ", cmc7=" + (cmc7 != null ? cmc7.toString() : "") +
                ", deposito= " + (deposito != null ? ObjectUtils.nullSafeToString(deposito.getId()) : "") +
                ", truncado=" + truncado +
                '}';
    }
}
