package com.zhouzh3.rocketmq.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.zhouzh3.rocketmq.util.BufferUtils.newString;

/**
 * @author haig
 */
@Component
@Slf4j
public class OrderMessageListener implements RocketMQMessageListener {

    @Override
    public String topic() {
        return "order-topic";
    }

    @Override
    public String tag() {
        return "create || cancel";
    }

    @Override
    public void onMessage(MessageView messageView) {
        // 业务逻辑处理
        log.info("===================Order message received: messageView={}, body={}", messageView, newString(messageView.getBody(), StandardCharsets.UTF_8));
    }
}
