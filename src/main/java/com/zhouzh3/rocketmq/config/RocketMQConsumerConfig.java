package com.zhouzh3.rocketmq.config;

import com.zhouzh3.rocketmq.router.PushConsumerRouter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haig
 */
@Slf4j
@Configuration
public class RocketMQConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    private final RocketMQProperties rocketMQProperties;
    private final PushConsumerRouter pushConsumerRouter;
    private PushConsumer pushConsumer;

    public RocketMQConsumerConfig(RocketMQProperties rocketMQProperties,
                                  PushConsumerRouter pushConsumerRouter) {
        this.rocketMQProperties = rocketMQProperties;
        this.pushConsumerRouter = pushConsumerRouter;
    }

    @PostConstruct
    public void startConsumer() throws ClientException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(rocketMQProperties.getEndpoint())
                .build();

        // 构造 FilterExpression 映射
        Map<String, FilterExpression> subscriptionExpressions = new HashMap<>(10);
        for (Map.Entry<String, String> entry : pushConsumerRouter.getSubscriptions().entrySet()) {
            subscriptionExpressions.put(entry.getKey(),
                    new FilterExpression(entry.getValue(), FilterExpressionType.TAG));
        }

        pushConsumer = provider.newPushConsumerBuilder()
                .setConsumptionThreadCount(20)
                .setClientConfiguration(clientConfiguration)
                .setConsumerGroup(rocketMQProperties.getConsumerConfig().getGroup())
                .setSubscriptionExpressions(subscriptionExpressions)
                .setMessageListener(messageView -> {
                    //如果能打印每条消息消费耗时，那么在排查消费慢等线上问题时，会更方便。但如果线上环境TPS很高，不建议开启，避免日志太多影响性能。
                    log.info("Consume message={}", messageView);
                    try {
                        pushConsumerRouter.route(messageView);
                        return ConsumeResult.SUCCESS;
                    } catch (Exception e) {
                        logger.error("Routing failed", e);
                        return ConsumeResult.FAILURE;
                    }
                })
                .build();

        logger.info("RocketMQ PushConsumer started. Subscriptions: {}", subscriptionExpressions);
    }

    @PreDestroy
    public void shutdown() {
        if (pushConsumer != null) {
            try {
                pushConsumer.close();
                logger.info("RocketMQ PushConsumer shutdown successfully");
            } catch (Exception e) {
                logger.error("Shutdown error", e);
            }
        }
    }
}
