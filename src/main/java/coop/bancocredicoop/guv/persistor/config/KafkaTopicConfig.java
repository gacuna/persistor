package coop.bancocredicoop.guv.persistor.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.correccion.topic}")
    private String correccionTopic;

    @Value(value = "${kafka.verificacion.topic}")
    private String verificacionTopic;

    @Value(value = "${kafka.correccion.partitions}")
    private Integer partitions;

    @Value(value = "${kafka.verificacion.partitions}")
    private Integer verificacionPartitions;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic correccionTopic() {
        return new NewTopic(correccionTopic,  partitions, (short) 1);
    }

    @Bean
    public NewTopic verificacionTopic() {
        return new NewTopic(verificacionTopic, verificacionPartitions, (short) 1);
    }
}
