package coop.bancocredicoop.guv.persistor.models;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Audited
@Entity
public class Deposito {

    @Id
    private Long id;

    @Column(name = "prioridadforzada")
    private Integer prioridadForzada;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deposito")
    private Set<Cheque> cheques;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipooperatoria")
    private TipoOperatoria tipoOperatoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrioridadForzada() {
        return prioridadForzada;
    }

    public void setPrioridadForzada(Integer prioridadForzada) {
        this.prioridadForzada = prioridadForzada;
    }

    public Set<Cheque> getCheques() {
        return cheques;
    }

    public void setCheques(Set<Cheque> cheques) {
        this.cheques = cheques;
    }

    public TipoOperatoria getTipoOperatoria() {
        return tipoOperatoria;
    }

    public void setTipoOperatoria(TipoOperatoria tipoOperatoria) {
        this.tipoOperatoria = tipoOperatoria;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Deposito{" +
                "id=" + id +
                ", prioridadForzada=" + prioridadForzada +
                ", tipoOperatoria=" + tipoOperatoria +
                ", estado=" + estado +
                '}';
    }

    public enum TipoOperatoria {
        //Diferidos
        CUSTODIA(true, '6', "CU"), COFA_DIF(true, '5', "CO"), DESCUENTO(true, '7', "CO"), CAUCION(true, '8', "CA"), CHWEB(true, '9', ""), VAL_NEG(true, '1', "CO"),
        CUSTODIA_REMOTA(true, '3', "CU"),
        //Del Dia
        COFA_DIA(false, '4', ""), AL_COBRO(false, '0', ""), NO_LIQ(false, 'N', ""), COOP_MUT(false, '2', ""), AJUSTE(false, 'A', ""), DEPOSITO_TARJETA_DD(false, 'T', "");

        private boolean diferido;
        private Character alternativaComercial;
        private String nombre;

        TipoOperatoria(boolean diferido, Character altComercial, String nombre) {
            this.diferido = diferido;
            this.alternativaComercial = altComercial;
            this.nombre = nombre;
        }

        public boolean isDiferido() {
            return diferido;
        }

        public static List<TipoOperatoria> getDiferidos(TipoOperatoria excluded) {
            List<TipoOperatoria> diferidosNames = new ArrayList<>();
            for (TipoOperatoria tipoOperatoria : TipoOperatoria.values()) {
                if (tipoOperatoria.isDiferido() && tipoOperatoria != excluded) {
                    diferidosNames.add(tipoOperatoria);
                }
            }
            return diferidosNames;
        }
    }

    public enum Estado {
        INGRESADO,
        VALIDAR_CMC7,
        CORREGIDO,
        BALANCEADO,
        EN_PROCESO,
        PRESENTADO,
        DERIVADO_FILIAL,
        ELIMINADO,
        REABIERTO,
        DIFERIDO_BALANCEADO;
    }
}
