package com.zhouzh3.rocketmq.router;

import com.zhouzh3.rocketmq.listener.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author haig
 */
@Component
public class PushConsumerRouter {

    /**
     * topic -> listeners
     */
    private final Map<String, List<RocketMQMessageListener>> rocketMQMessageListeners = new HashMap<>();

    public PushConsumerRouter(List<RocketMQMessageListener> listeners) {
        for (RocketMQMessageListener listener : listeners) {
            rocketMQMessageListeners.computeIfAbsent(listener.topic(), k -> new ArrayList<>()).add(listener);
        }
    }

    public void route(MessageView messageView) {
        String topic = messageView.getTopic();
        String tag = messageView.getTag().orElse("");

        //  根据 topic 和 tag 匹配
        List<RocketMQMessageListener> listeners = rocketMQMessageListeners.get(topic);
        if (listeners == null) {
            return;
        }

        for (RocketMQMessageListener listener : listeners) {
            String tagExpr = listener.tag();
            if ("*".equals(tagExpr) || tagMatch(tagExpr, tag)) {
                listener.onMessage(messageView);
            }
        }
    }

    private boolean tagMatch(String tagExpr, String tag) {
        String[] parts = tagExpr.split(Pattern.quote("||"));
        for (String part : parts) {
            if (part.trim().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回所有订阅的 topic 和 tag 表达式，用于构建订阅参数
     */
    public Map<String, String> getSubscriptions() {
        Map<String, String> subscriptions = new HashMap<>(10);
        for (Map.Entry<String, List<RocketMQMessageListener>> entry : rocketMQMessageListeners.entrySet()) {
            String topic = entry.getKey();
            Set<String> tags = new HashSet<>();
            for (RocketMQMessageListener listener : entry.getValue()) {
                String expr = listener.tag();
                if ("*".equals(expr)) {
                    tags.clear();
                    tags.add("*");
                    break;
                }
                tags.addAll(Arrays.asList(expr.split("\\|\\|")));
            }
            subscriptions.put(topic, String.join(" || ", tags));
        }
        return subscriptions;
    }
}
