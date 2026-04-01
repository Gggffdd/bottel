package com.example.messenger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.adapters.UsersAdapter;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.utils.PreferencesManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";
    private RecyclerView rvUsers;
    private UsersAdapter adapter;
    private List<ApiService.UserSearchResult> userList = new ArrayList<>();
    private PreferencesManager prefManager;
    private EditText etSearch;
    private TextView tvEmptyResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        Log.d(TAG, "View inflated");
        
        prefManager = new PreferencesManager(getContext());
        Log.d(TAG, "PreferencesManager created, token: " + (prefManager.getToken() != null));
        
        rvUsers = view.findViewById(R.id.rvUsers);
        etSearch = view.findViewById(R.id.etSearch);
        tvEmptyResults = view.findViewById(R.id.tvEmptyResults);
        
        Log.d(TAG, "Views found: rvUsers=" + (rvUsers != null) + 
              ", etSearch=" + (etSearch != null) + 
              ", tvEmptyResults=" + (tvEmptyResults != null));
        
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsersAdapter(userList, user -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("withUser", user.username);
            intent.putExtra("withUserName", user.displayName != null ? user.displayName : user.username);
            startActivity(intent);
        });
        rvUsers.setAdapter(adapter);
        Log.d(TAG, "Adapter set");
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                Log.d(TAG, "Search query: " + query);
                if (query.isEmpty()) {
                    userList.clear();
                    adapter.notifyDataSetChanged();
                    showEmptyResults(false);
                } else {
                    if (query.startsWith("@")) {
                        query = query.substring(1);
                    }
                    searchUsers(query);
                }
            }
        });
        
        Log.d(TAG, "onCreateView finished");
        return view;
    }
    
    private void searchUsers(String query) {
        if (query.isEmpty()) {
            userList.clear();
            adapter.notifyDataSetChanged();
            showEmptyResults(false);
            return;
        }
        
        Log.d(TAG, "searchUsers: " + query);
        tvEmptyResults.setText("Поиск...");
        tvEmptyResults.setVisibility(View.VISIBLE);
        
        ApiClient.getApi().searchUsers("Bearer " + prefManager.getToken(), query).enqueue(new Callback<List<ApiService.UserSearchResult>>() {
            @Override
            public void onResponse(Call<List<ApiService.UserSearchResult>> call, Response<List<ApiService.UserSearchResult>> response) {
                Log.d(TAG, "searchUsers onResponse: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    showEmptyResults(userList.isEmpty());
                    Log.d(TAG, "Found " + userList.size() + " users");
                } else {
                    showEmptyResults(true);
                    Toast.makeText(getContext(), "Ошибка поиска", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<ApiService.UserSearchResult>> call, Throwable t) {
                Log.e(TAG, "searchUsers onFailure: " + t.getMessage());
                showEmptyResults(true);
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showEmptyResults(boolean show) {
        if (show) {
            tvEmptyResults.setText("🔍 Ничего не найдено\nПопробуйте другой запрос");
            tvEmptyResults.setVisibility(View.VISIBLE);
            rvUsers.setVisibility(View.GONE);
        } else {
            tvEmptyResults.setVisibility(View.GONE);
            rvUsers.setVisibility(View.VISIBLE);
        }
    }
}
