package com.example.kafkaproducer.configuration;

import com.example.kafkaproducer.model.FixMsgModel;
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
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> producerFactoryConfigs = new HashMap<>();
        producerFactoryConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        producerFactoryConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerFactoryConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerFactoryConfigs);
    }

    @Bean
    public KafkaTemplate<String, String> template() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, FixMsgModel> fixMsgModelProducerFactory() {
        Map<String, Object> fixMsgProducerFactoryConfigs = new HashMap<>();
        fixMsgProducerFactoryConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        fixMsgProducerFactoryConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        fixMsgProducerFactoryConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        fixMsgProducerFactoryConfigs.put(JsonSerializer.ADD_TYPE_INFO_HEADERS,false);
        fixMsgProducerFactoryConfigs.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        return new DefaultKafkaProducerFactory<>(fixMsgProducerFactoryConfigs);
    }

    @Bean
    public KafkaTemplate<String, FixMsgModel> fixMsgModelKafkaTemplate() {
        return new KafkaTemplate<>(fixMsgModelProducerFactory());
    }

}
