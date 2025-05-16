package com.baidu.notification;

public interface Notifier {
    void send(NotifyMessage message) throws Exception;
}