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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        prefManager = new PreferencesManager(this);

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
    }

    private void sendCode() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
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
                    tvStatus.setText("Код отправлен на email");
                    btnSendCode.setEnabled(false);
                    btnVerify.setEnabled(true);
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Ошибка отправки";
                    tvStatus.setText(msg);
                    btnSendCode.setEnabled(true);
                    Toast.makeText(EmailVerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                tvStatus.setText("Сетевая ошибка");
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

        tvStatus.setText("Проверка кода...");
        btnVerify.setEnabled(false);

        ApiService.VerifyCodeRequest req = new ApiService.VerifyCodeRequest();
        req.email = email;
        req.code = code;

        ApiClient.getApi().verifyCode(req).enqueue(new Callback<ApiService.VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<ApiService.VerifyCodeResponse> call, Response<ApiService.VerifyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    prefManager.saveUser(email, response.body().token);
                    Toast.makeText(EmailVerificationActivity.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
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
                tvStatus.setText("Сетевая ошибка");
                btnVerify.setEnabled(true);
                Toast.makeText(EmailVerificationActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
