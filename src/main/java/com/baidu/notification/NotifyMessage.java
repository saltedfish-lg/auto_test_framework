package com.baidu.notification;

import java.util.List;

public class NotifyMessage {

    private String title;
    private String content;
    private List<String> atUsers;
    private String format; // text / markdown / html

    public NotifyMessage(String title, String content, List<String> atUsers, String format) {
        this.title = title;
        this.content = content;
        this.atUsers = atUsers;
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getAtUsers() {
        return atUsers;
    }

    public String getFormat() {
        return format;
    }
}
