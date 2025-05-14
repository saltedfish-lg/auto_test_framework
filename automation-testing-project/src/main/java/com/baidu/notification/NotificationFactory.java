package com.baidu.notification;

/**
 * 通知工厂类
 */
public class NotificationFactory {

    public static Notifier getNotifier(String type, String webhookUrl) {
        if ("dingtalk".equalsIgnoreCase(type)) {
            return new DingTalkNotifier(webhookUrl);
        } else if ("wechat".equalsIgnoreCase(type)) {
            return new WeChatNotifier(webhookUrl);
        } else {
            throw new IllegalArgumentException("不支持的通知类型: " + type);
        }
    }
}
