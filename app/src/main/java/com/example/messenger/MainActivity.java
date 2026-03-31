package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.adapters.UsersAdapter;
import com.example.messenger.models.User;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.utils.PreferencesManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    private UsersAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private PreferencesManager prefManager;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PreferencesManager(this);
        if (!prefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvUsers = findViewById(R.id.rvUsers);
        etSearch = findViewById(R.id.etSearch);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(userList, user -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("withUser", user.getUsername());
            startActivity(intent);
        });
        rvUsers.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                searchUsers(s.toString());
            }
        });
    }

    private void searchUsers(String query) {
        if (query.isEmpty()) {
            userList.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        ApiClient.getApi().searchUsers("Bearer " + prefManager.getToken(), query).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    for (String username : response.body()) {
                        userList.add(new User(username));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка поиска", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
