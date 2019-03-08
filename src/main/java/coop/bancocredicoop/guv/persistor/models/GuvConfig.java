package coop.bancocredicoop.guv.persistor.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "GUVCONFIG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GuvConfig {
    @Id
    @Pattern(regexp = "^[a-zA-Z0-9-. _;]*$", message = "No puede contener caracteres especiales excepto los siguientes -_.;")
    private String id;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-.:;@ _\\/\\\\]*$", message = "No puede contener caracteres especiales excepto los siguientes /\\-_.:;@")
    private String valor;

    public String getId() {
        return id;
    }

    public GuvConfig setId(String id) {
        this.id = id;
        return this;
    }

    public String getValor() {
        return valor;
    }

    public GuvConfig setValor(String valor) {
        this.valor = valor;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GuvConfig guvConfig = (GuvConfig) o;

        return new EqualsBuilder()
                .append(id, guvConfig.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

}
