package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

    private EditText etPhone, etEmail, etPassword, etCode;
    private Button btnSendCode, btnVerify;
    private TextView tvStatus;
    private PreferencesManager prefManager;
    
    private boolean codeSent = false;
    private String tempEmail;
    private String tempPhone;
    private String tempPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefManager = new PreferencesManager(this);
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCode = findViewById(R.id.etCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        tvStatus = findViewById(R.id.tvStatus);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnVerify.setOnClickListener(v -> verifyCode());
    }
    
    private void applyTheme() {
        String theme = prefManager.getTheme();
        if (theme.equals("dark")) {
            setTheme(R.style.Theme_Messenger_Dark);
        } else {
            setTheme(R.style.Theme_Messenger);
        }
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Введите номер телефона");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Введите пароль");
            return;
        }
        if (password.length() < 4) {
            etPassword.setError("Пароль должен быть не менее 4 символов");
            return;
        }

        tempPhone = phone;
        tempEmail = email;
        tempPassword = password;

        btnSendCode.setEnabled(false);
        tvStatus.setText("Отправка кода...");

        ApiService.SendCodeRequest req = new ApiService.SendCodeRequest();
        req.email = email;

        ApiClient.getApi().sendCode(req).enqueue(new Callback<ApiService.SimpleResponse>() {
            @Override
            public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    tvStatus.setText("Код отправлен на " + email);
                    codeSent = true;
                    btnSendCode.setEnabled(false);
                    btnVerify.setEnabled(true);
                    etCode.requestFocus();
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Ошибка";
                    tvStatus.setText(msg);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                tvStatus.setText("Ошибка сети");
                btnSendCode.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyCode() {
        String code = etCode.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            etCode.setError("Введите код");
            return;
        }

        btnVerify.setEnabled(false);
        tvStatus.setText("Проверка кода...");

        ApiService.VerifyCodeRequest req = new ApiService.VerifyCodeRequest();
        req.email = tempEmail;
        req.code = code;

        ApiClient.getApi().verifyCode(req).enqueue(new Callback<ApiService.VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<ApiService.VerifyCodeResponse> call, Response<ApiService.VerifyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Код подтверждён, теперь регистрируем пользователя
                    ApiService.RegisterRequest registerReq = new ApiService.RegisterRequest();
                    registerReq.phone = tempPhone;
                    registerReq.email = tempEmail;
                    registerReq.password = tempPassword;
                    
                    ApiClient.getApi().register(registerReq).enqueue(new Callback<ApiService.LoginResponse>() {
                        @Override
                        public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().success) {
                                prefManager.saveUser(tempEmail, response.body().token);
                                prefManager.saveDisplayName(tempEmail.split("@")[0]);
                                Toast.makeText(RegisterActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                String msg = response.body() != null && response.body().error != null ? response.body().error : "Ошибка регистрации";
                                tvStatus.setText(msg);
                                btnVerify.setEnabled(true);
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                            tvStatus.setText("Ошибка сети");
                            btnVerify.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Неверный код";
                    tvStatus.setText(msg);
                    btnVerify.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.VerifyCodeResponse> call, Throwable t) {
                tvStatus.setText("Ошибка сети");
                btnVerify.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
