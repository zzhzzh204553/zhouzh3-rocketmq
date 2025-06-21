package com.zhouzh3.rocketmq.listener;

import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author haig
 */
public interface RocketMQMessageListener {

    /**
     * 监听的主题
     *
     * @return 监听的主题
     */
    String topic();

    /**
     * 监听的 Tag 表达式（支持 "*" 或 "TagA || TagB"）
     * @return 监听的 Tag 表达式
     */
    String tag();

    /**
     * 消息处理逻辑
     *
     * @param messageView 消息
     */
    void onMessage(MessageView messageView);
}
