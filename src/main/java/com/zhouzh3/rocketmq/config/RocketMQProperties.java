package com.zhouzh3.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author haig
 */
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
@Data
public class RocketMQProperties {

    private String endpoint;

    private ProducerConfig producerConfig;

    private ConsumerConfig consumerConfig;

    @Data
    public static class ProducerConfig {
        private String topic;
    }

    @Data
    public static class ConsumerConfig {
        private String group;
        private String topic;
        private String tag;

        private int consumptionThreadCount = 20;
    }

}
