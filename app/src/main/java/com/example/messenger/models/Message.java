package com.example.messenger.models;

public class Message {
    private String fromUser;
    private String toUser;
    private String message;
    private long timestamp;

    public Message(String fromUser, String toUser, String message, long timestamp) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getFromUser() { return fromUser; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
