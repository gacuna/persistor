package coop.bancocredicoop.proyectos.guvpersistor.listener;

import coop.bancocredicoop.proyectos.guvpersistor.model.Cheque;
import coop.bancocredicoop.proyectos.guvpersistor.repository.ChequeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @Autowired
    ChequeRepository chequeRepository;

    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "correcciones_topic", groupId = "json_group_id")
    public void consume(Cheque cheque) {
        logger.info("Cheque para actualizar: " + cheque.getId());
        chequeRepository.save(cheque);
        logger.info("Cheque " + cheque.getId() + " actulizado.");
    }

}
