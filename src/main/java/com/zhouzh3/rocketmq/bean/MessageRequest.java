package com.zhouzh3.rocketmq.bean;

import lombok.Data;

/**
 * @author haig
 * messageKey用来保存业务ID
 */
@Data
public class MessageRequest {

    private String topic;

    private String tag;

    private String body;

    private String messageKey;

}
