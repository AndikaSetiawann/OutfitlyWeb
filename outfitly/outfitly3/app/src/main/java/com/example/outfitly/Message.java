package com.example.outfitly;

import java.util.List;

public class Message {
    private String content;
    private boolean isUser;
    private List<String> quickReplies;

    public Message(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
        this.quickReplies = null;
    }

    public Message(String content, boolean isUser, List<String> quickReplies) {
        this.content = content;
        this.isUser = isUser;
        this.quickReplies = quickReplies;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }

    public List<String> getQuickReplies() {
        return quickReplies;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public void setQuickReplies(List<String> quickReplies) {
        this.quickReplies = quickReplies;
    }

    public boolean hasQuickReplies() {
        return quickReplies != null && !quickReplies.isEmpty();
    }
}
