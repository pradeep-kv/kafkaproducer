package com.example.kafkaproducer.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProducerAdminClient {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
    AdminClient client = null;
    ProducerAdminClient(){
        if(client == null){
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
            client = AdminClient.create(configs);
        }
    }

    public AdminClient getAdminClient(){
        return this.client;
    }

}
