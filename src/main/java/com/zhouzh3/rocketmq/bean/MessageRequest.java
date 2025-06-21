package com.zhouzh3.rocketmq.bean;

import lombok.Data;

/**
 * @author haig
 */
@Data
public class MessageRequest {

    private String topic;

    private String tag;

    private String body;

    private String messageKey;

}
