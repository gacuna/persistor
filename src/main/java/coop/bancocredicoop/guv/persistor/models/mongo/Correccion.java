package coop.bancocredicoop.guv.persistor.models.mongo;

import coop.bancocredicoop.guv.persistor.models.CMC7;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Document
public class Correccion implements Serializable {

    protected Long id;
    protected BigDecimal importe;
    protected Date fechaDiferida;
    protected String cuit;
    protected Integer codMoneda;
    protected CMC7 cmc7;
    protected LocalDateTime createdAt;
    protected Boolean truncado;

    public Correccion() {}

    public Correccion(Long id, BigDecimal importe, Date fechaDiferida, String cuit, Integer codMoneda, CMC7 cmc7) {
        this.id = id;
        this.importe = importe;
        this.fechaDiferida = fechaDiferida;
        this.cuit = cuit;
        this.codMoneda = codMoneda;
        this.cmc7 = cmc7;
        this.createdAt = null;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getTruncado() {
        return truncado;
    }

    public void setTruncado(Boolean truncado) {
        this.truncado = truncado;
    }

    @Override
    public String toString() {
        return "Correccion{" +
                "id=" + id +
                ", importe=" + importe +
                ", fechaDiferida=" + fechaDiferida +
                ", cuit='" + cuit + '\'' +
                ", codMoneda=" + codMoneda +
                ", cmc7=" + cmc7 +
                ", truncado=" + truncado +
                ", createdAt=" + createdAt +
                '}';
    }
}
