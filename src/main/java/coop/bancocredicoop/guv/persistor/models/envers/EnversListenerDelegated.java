package coop.bancocredicoop.guv.persistor.models.envers;

import coop.bancocredicoop.guv.persistor.config.ApplicationContextProvider;
import org.hibernate.envers.RevisionListener;

/**
 * Created by squintanilla on 21/07/2017.
 */
public class EnversListenerDelegated implements RevisionListener {

    public static final String USERGUV = "GUV-PERSISTOR";

    @Override
    public void newRevision(Object revisionEntity) {
        GuvEnversListener guvEnversListener = (GuvEnversListener) ApplicationContextProvider.getApplicationContext().getBean("guvEnversListener");
        guvEnversListener.updateRevision(revisionEntity);
    }
}
