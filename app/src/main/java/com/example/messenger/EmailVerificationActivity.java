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

public class EmailVerificationActivity extends AppCompatActivity {

    private EditText etEmail, etCode;
    private Button btnSendCode, btnVerify;
    private TextView tvStatus;
    private PreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefManager = new PreferencesManager(this);
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Проверяем, не залогинен ли уже пользователь
        if (prefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etCode = findViewById(R.id.etCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        tvStatus = findViewById(R.id.tvStatus);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnVerify.setOnClickListener(v -> verifyCode());
        
        etEmail.requestFocus();
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
        String email = etEmail.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            return;
        }

        btnSendCode.setEnabled(false);
        tvStatus.setText("Отправка кода...");

        ApiService.SendCodeRequest req = new ApiService.SendCodeRequest();
        req.email = email;

        ApiClient.getApi().sendCode(req).enqueue(new Callback<ApiService.SimpleResponse>() {
            @Override
            public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    tvStatus.setText("Код отправлен на " + email);
                    btnSendCode.setEnabled(false);
                    btnVerify.setEnabled(true);
                    etCode.requestFocus();
                    Toast.makeText(EmailVerificationActivity.this, "Код отправлен! Проверьте почту", Toast.LENGTH_LONG).show();
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Ошибка";
                    tvStatus.setText(msg);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(EmailVerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                tvStatus.setText("Ошибка сети");
                btnSendCode.setEnabled(true);
                Toast.makeText(EmailVerificationActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyCode() {
        String email = etEmail.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            etCode.setError("Введите код");
            return;
        }
        
        if (code.length() != 6) {
            etCode.setError("Код должен состоять из 6 цифр");
            return;
        }

        btnVerify.setEnabled(false);
        tvStatus.setText("Проверка кода...");

        ApiService.VerifyCodeRequest req = new ApiService.VerifyCodeRequest();
        req.email = email;
        req.code = code;

        ApiClient.getApi().verifyCode(req).enqueue(new Callback<ApiService.VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<ApiService.VerifyCodeResponse> call, Response<ApiService.VerifyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    ApiService.VerifyCodeResponse resp = response.body();
                    
                    // Сохраняем данные пользователя
                    if (resp.user != null) {
                        prefManager.saveUserData(
                            resp.user.email,
                            resp.user.username,
                            resp.user.displayName != null ? resp.user.displayName : resp.user.username,
                            resp.user.bio != null ? resp.user.bio : "",
                            resp.user.birthday != null ? resp.user.birthday : "",
                            resp.user.avatar != null ? resp.user.avatar : "",
                            resp.user.theme != null ? resp.user.theme : "light"
                        );
                    } else {
                        prefManager.saveUser(email, resp.token);
                    }
                    
                    Toast.makeText(EmailVerificationActivity.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                    
                    // Переход в главное меню
                    Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Неверный код";
                    tvStatus.setText(msg);
                    btnVerify.setEnabled(true);
                    Toast.makeText(EmailVerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.VerifyCodeResponse> call, Throwable t) {
                tvStatus.setText("Ошибка сети");
                btnVerify.setEnabled(true);
                Toast.makeText(EmailVerificationActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
