package coop.bancocredicoop.guv.persistor.config;

import coop.bancocredicoop.guv.persistor.actors.BalanceMessage;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.actors.VerifyMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, UpdateMessage> updateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(buildProps());
    }

    @Bean
    public ProducerFactory<String, VerifyMessage> verificationProducerFactory() {
        return new DefaultKafkaProducerFactory<>(buildProps());
    }

    @Bean
    public ProducerFactory<String, BalanceMessage> balancingProducerFactory() {
        return new DefaultKafkaProducerFactory<>(buildProps());
    }

    @Bean
    public KafkaTemplate<String, UpdateMessage> updateMessageTemplate() {
        return new KafkaTemplate<>(updateProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, VerifyMessage> verificationMessageTemplate() {
        return new KafkaTemplate<>(verificationProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, BalanceMessage> balanceMessageKafkaTemplate() {
        return new KafkaTemplate<>(balancingProducerFactory());
    }

    private Map<String, Object> buildProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

}
