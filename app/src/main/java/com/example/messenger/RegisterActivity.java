package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.utils.PreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etPhone, etUsername, etPassword;
    private Button btnRegister;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvInfo = findViewById(R.id.tvInfo);

        btnRegister.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                tvInfo.setText("Username может содержать только буквы, цифры и _");
                return;
            }

            register(phone, username, password);
        });
    }

    private void register(String phone, String username, String password) {
        ApiService.RegisterRequest req = new ApiService.RegisterRequest();
        req.phone = phone;
        req.username = username;
        req.password = password;
        ApiClient.getApi().register(req).enqueue(new Callback<ApiService.LoginResponse>() {
            @Override
            public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    new PreferencesManager(RegisterActivity.this).saveUser(response.body().username, response.body().token);
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Ошибка регистрации";
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Сетевая ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
