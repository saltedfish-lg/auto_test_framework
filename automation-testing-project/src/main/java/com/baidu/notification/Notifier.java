package com.baidu.notification;

/**
 * 通知接口
 */
public interface Notifier {
    void send(String title, String content, String reportUrl);
}
