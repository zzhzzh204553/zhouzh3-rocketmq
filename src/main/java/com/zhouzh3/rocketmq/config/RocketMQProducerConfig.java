package com.zhouzh3.rocketmq.config;

import jakarta.annotation.PreDestroy;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author haig
 */
@Configuration
public class RocketMQProducerConfig {

    private Producer producer;

    @Bean
    public Producer rocketMQProducer(RocketMQProperties rocketMQProperties) throws ClientException {
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(rocketMQProperties.getEndpoint())
                .build();

        this.producer = ClientServiceProvider.loadService().newProducerBuilder()
                .setTopics(rocketMQProperties.getProducerConfig().getTopic())
                .setClientConfiguration(configuration)
                .build();

        return producer;
    }

    @PreDestroy
    public void shutdown() {
        if (producer != null) {
            try {
                producer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
