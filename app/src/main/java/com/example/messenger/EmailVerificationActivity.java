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
        // Применяем тему перед загрузкой layout
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

        // Инициализируем элементы
        etEmail = findViewById(R.id.etEmail);
        etCode = findViewById(R.id.etCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        tvStatus = findViewById(R.id.tvStatus);

        // Обработчики кнопок
        btnSendCode.setOnClickListener(v -> sendCode());
        btnVerify.setOnClickListener(v -> verifyCode());
        
        // Автофокус на поле email
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
        
        // Валидация email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            etEmail.requestFocus();
            return;
        }

        // Блокируем кнопку во время отправки
        btnSendCode.setEnabled(false);
        btnSendCode.setText("Отправка...");
        tvStatus.setText("Отправка кода на " + email + "...");
        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        // Создаём запрос
        ApiService.SendCodeRequest req = new ApiService.SendCodeRequest();
        req.email = email;

        // Отправляем запрос на сервер
        ApiClient.getApi().sendCode(req).enqueue(new Callback<ApiService.SimpleResponse>() {
            @Override
            public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Код отправлен успешно
                    tvStatus.setText("Код отправлен на " + email);
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    btnSendCode.setEnabled(false);
                    btnVerify.setEnabled(true);
                    etCode.requestFocus();
                    Toast.makeText(EmailVerificationActivity.this, "Код отправлен! Проверьте почту", Toast.LENGTH_LONG).show();
                } else {
                    // Ошибка от сервера
                    String msg = response.body() != null && response.body().error != null 
                        ? response.body().error 
                        : "Ошибка отправки кода";
                    tvStatus.setText(msg);
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnSendCode.setEnabled(true);
                    btnSendCode.setText("Отправить код");
                    Toast.makeText(EmailVerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                // Ошибка сети
                tvStatus.setText("Ошибка сети: " + t.getMessage());
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btnSendCode.setEnabled(true);
                btnSendCode.setText("Отправить код");
                Toast.makeText(EmailVerificationActivity.this, "Сетевая ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyCode() {
        String email = etEmail.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        // Валидация кода
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Введите код");
            etCode.requestFocus();
            return;
        }
        
        if (code.length() != 6) {
            etCode.setError("Код должен состоять из 6 цифр");
            etCode.requestFocus();
            return;
        }

        // Блокируем кнопку во время проверки
        btnVerify.setEnabled(false);
        btnVerify.setText("Проверка...");
        tvStatus.setText("Проверка кода...");
        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        // Создаём запрос
        ApiService.VerifyCodeRequest req = new ApiService.VerifyCodeRequest();
        req.email = email;
        req.code = code;

        // Отправляем запрос на сервер
        ApiClient.getApi().verifyCode(req).enqueue(new Callback<ApiService.VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<ApiService.VerifyCodeResponse> call, Response<ApiService.VerifyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Успешная авторизация
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
                    
                    tvStatus.setText("Вход выполнен!");
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    Toast.makeText(EmailVerificationActivity.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                    
                    // Переходим в главное меню
                    startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Ошибка проверки кода
                    String msg = response.body() != null && response.body().error != null 
                        ? response.body().error 
                        : "Неверный код";
                    tvStatus.setText(msg);
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnVerify.setEnabled(true);
                    btnVerify.setText("Подтвердить");
                    Toast.makeText(EmailVerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.VerifyCodeResponse> call, Throwable t) {
                // Ошибка сети
                tvStatus.setText("Ошибка сети: " + t.getMessage());
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btnVerify.setEnabled(true);
                btnVerify.setText("Подтвердить");
                Toast.makeText(EmailVerificationActivity.this, "Сетевая ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
