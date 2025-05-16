package com.baidu.notification;

import com.baidu.utils.HttpUtils;

public class DingTalkNotifier implements Notifier {

    @Override
    public void send(NotifyMessage message) throws Exception {
        if (message.getToken() == null || message.getToken().isEmpty()) {
            throw new IllegalArgumentException("钉钉 webhook token 未配置");
        }

        String url = message.getToken();
        String jsonPayload = "{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"" + message.getTitle() + "\", \"text\": \"" +
                message.getContent().replace("\"", "\\\"").replace("\n", "\\n") + "\" } }";

        String response = HttpUtils.postJson(url, jsonPayload);
        System.out.println("DingTalk 通知响应: " + response);
    }
}