package com.example.messenger.network;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private OnNewMessageListener messageListener;

    public interface OnNewMessageListener { void onNewMessage(String from, String message, long timestamp); }

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) instance = new SocketManager();
        return instance;
    }

    public void connect(String token) {
        try {
            IO.Options opts = new IO.Options();
            opts.auth = new JSONObject().put("token", token);
            socket = IO.socket("http://95.85.242.13:3000", opts);
            socket.on(Socket.EVENT_CONNECT, args -> System.out.println("Connected"));
            socket.on("new_message", args -> {
                JSONObject data = (JSONObject) args[0];
                String from = data.optString("from");
                String message = data.optString("message");
                long timestamp = data.optLong("timestamp");
                if (messageListener != null) messageListener.onNewMessage(from, message, timestamp);
            });
            socket.connect();
        } catch (URISyntaxException e) { e.printStackTrace(); }
    }

    public void sendMessage(String to, String message) {
        if (socket != null && socket.connected()) {
            socket.emit("private_message", new JSONObject().put("to", to).put("message", message));
        }
    }

    public void disconnect() { if (socket != null) socket.disconnect(); }
    public void setOnNewMessageListener(OnNewMessageListener listener) { this.messageListener = listener; }
}
