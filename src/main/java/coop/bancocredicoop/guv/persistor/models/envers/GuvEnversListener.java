package coop.bancocredicoop.guv.persistor.models.envers;

public interface GuvEnversListener {
    void updateRevision(Object revision);
}
