package com.zhouzh3.rocketmq.controller;

import com.zhouzh3.rocketmq.bean.MessageRequest;
import com.zhouzh3.rocketmq.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haig
 */
@Slf4j
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageProducerService messageProducerService;

    public MessageController(MessageProducerService messageProducerService) {
        this.messageProducerService = messageProducerService;
    }

    @PostMapping
    public String send(@RequestBody MessageRequest request) {
        log.info("请求参数 = {}", request);

        return messageProducerService.sendMessage(request);
    }
}
