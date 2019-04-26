package coop.bancocredicoop.guv.persistor.config;

import coop.bancocredicoop.guv.persistor.models.AuditRevision;
import coop.bancocredicoop.guv.persistor.models.envers.EnversListenerDelegated;
import coop.bancocredicoop.guv.persistor.models.envers.GuvEnversListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("guvEnversListener")
public class GuvEnversListenerImpl implements GuvEnversListener {

    @Override
    public void updateRevision(Object revision) {
        AuditRevision auditRevision = (AuditRevision) revision;
        String username = EnversListenerDelegated.USERGUV;
        //TODO Ver como obtener el usuario!
        auditRevision.setUsuario(username);
        auditRevision.setRevTimestamp(new Date());
    }
}
