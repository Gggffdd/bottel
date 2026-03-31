package com.example.messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.adapters.MessagesAdapter;
import com.example.messenger.models.Message;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.network.SocketManager;
import com.example.messenger.utils.PreferencesManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText etMessage;
    private ImageButton btnSend;
    private String withUser;
    private PreferencesManager prefManager;
    private SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        withUser = getIntent().getStringExtra("withUser");
        prefManager = new PreferencesManager(this);
        socketManager = SocketManager.getInstance();
        socketManager.connect(prefManager.getToken());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("@" + withUser);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(messageList, prefManager.getUsername());
        rvMessages.setAdapter(adapter);

        loadMessages();

        socketManager.setOnNewMessageListener((from, message, timestamp) -> {
            if (from.equals(withUser)) {
                runOnUiThread(() -> {
                    messageList.add(new Message(from, prefManager.getUsername(), message, timestamp));
                    adapter.notifyItemInserted(messageList.size() - 1);
                    rvMessages.scrollToPosition(messageList.size() - 1);
                });
            }
        });

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            socketManager.sendMessage(withUser, text);
            messageList.add(new Message(prefManager.getUsername(), withUser, text, System.currentTimeMillis() / 1000));
            adapter.notifyItemInserted(messageList.size() - 1);
            rvMessages.scrollToPosition(messageList.size() - 1);
            etMessage.setText("");
        });
    }

    private void loadMessages() {
        ApiClient.getApi().getMessages("Bearer " + prefManager.getToken(), withUser).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(messageList.size() - 1);
                }
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Ошибка загрузки сообщений", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.disconnect();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
