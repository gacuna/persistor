package coop.bancocredicoop.guv.persistor.models;

import coop.bancocredicoop.guv.persistor.models.envers.EnversListenerDelegated;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@RevisionEntity(EnversListenerDelegated.class)
public class AuditRevision extends DefaultRevisionEntity {

    private Date revTimestamp;
    private String usuario;

    public Date getRevTimestamp() {
        return revTimestamp;
    }

    public void setRevTimestamp(Date revTimestamp) {
        this.revTimestamp = revTimestamp;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
