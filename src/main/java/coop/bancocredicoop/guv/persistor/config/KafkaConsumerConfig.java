package coop.bancocredicoop.guv.persistor.config;

import coop.bancocredicoop.guv.persistor.actors.BalanceMessage;
import coop.bancocredicoop.guv.persistor.actors.UpdateMessage;
import coop.bancocredicoop.guv.persistor.actors.VerifyMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.persistor.groupId}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, UpdateMessage> consumerFactory() {
        Map<String, Object> props = buildProps();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "update-consumer-" + UUID.randomUUID());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(UpdateMessage.class));
    }

    @Bean
    public ConsumerFactory<String, VerifyMessage> consumerFactory2() {
        Map<String, Object> props = buildProps();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "verify-consumer-" + UUID.randomUUID());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(VerifyMessage.class));
    }

    @Bean
    public ConsumerFactory<String, BalanceMessage> consumerFactory3() {
        Map<String, Object> props = buildProps();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "balance-consumer-" + UUID.randomUUID());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(BalanceMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UpdateMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdateMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VerifyMessage> verifyMessageConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VerifyMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory2());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BalanceMessage> balanceMessageConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BalanceMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory3());
        return factory;
    }

    private Map<String, Object> buildProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

}
