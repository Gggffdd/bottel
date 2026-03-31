package com.example.messenger.network;

public class SocketManager {
    private static SocketManager instance;
    private OnNewMessageListener messageListener;

    public interface OnNewMessageListener {
        void onNewMessage(String from, String message, long timestamp);
    }

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) instance = new SocketManager();
        return instance;
    }

    public void connect(String token) {
        System.out.println("SocketManager: connect called (stub)");
    }

    public void sendMessage(String to, String message) {
        System.out.println("SocketManager: sendMessage called (stub)");
    }

    public void disconnect() {
        System.out.println("SocketManager: disconnect called (stub)");
    }

    public void setOnNewMessageListener(OnNewMessageListener listener) {
        this.messageListener = listener;
    }
}
