package me.gnevilkoko.Utils;

import me.gnevilkoko.Enums.ChatStatus;

public class User {
    private long userId;
    private String language;
    private ChatStatus chatStatus;
    private String tempData;

    public User(long userId, String language, ChatStatus chatStatus, String tempData) {
        this.userId = userId;
        this.language = language;
        this.chatStatus = chatStatus;
        this.tempData = tempData;
    }

    public User() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus;
    }

    public String getTempData() {
        return tempData;
    }

    public void setTempData(String tempData) {
        this.tempData = tempData;
    }
}
