package coop.bancocredicoop.guv.persistor.repositories;

import coop.bancocredicoop.guv.persistor.models.GuvConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuvConfigRepository extends JpaRepository<GuvConfig, String> {
}
