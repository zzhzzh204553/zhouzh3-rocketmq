package com.zhouzh3.rocketmq.service;

import com.zhouzh3.rocketmq.bean.MessageRequest;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author haig
 */
@Service
public class MessageProducerService {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducerService.class);

    private final ClientServiceProvider provider;

    private final Producer producer;

    public MessageProducerService(Producer producer) {
        this.provider = ClientServiceProvider.loadService();
        this.producer = producer;
    }

    public String sendMessage(MessageRequest request) {
        try {
            Message message = provider.newMessageBuilder()
                    .setTopic(request.getTopic())
                    .setKeys(request.getMessageKey())
                    .setTag(request.getTag())
                    .setBody(request.getBody().getBytes())
                    .build();

            SendReceipt receipt = producer.send(message);
            logger.info("Message sent, ID: {}", receipt.getMessageId());
            return receipt.getMessageId().toString();
        } catch (ClientException e) {
            logger.error("Failed to send message", e);
            return "Failed: " + e.getMessage();
        }
    }
}
