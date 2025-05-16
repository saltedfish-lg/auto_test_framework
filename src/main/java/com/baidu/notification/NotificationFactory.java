package com.baidu.notification;

public class NotificationFactory {

    public static Notifier getNotifier(String platform) {
        if (platform == null || platform.trim().isEmpty()) {
            throw new IllegalArgumentException("未指定通知平台（notify.platform）");
        }
        switch (platform.trim().toLowerCase()) {
            case "wechat":
                return new WeChatNotifier();
            case "dingtalk":
                return new DingTalkNotifier();
            default:
                throw new UnsupportedOperationException("不支持的通知平台: " + platform);
        }
    }
}