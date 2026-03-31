package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private EditText etPhone, etCode;
    private Button btnSendCode, btnVerify;
    private TextView tvStatus;
    
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    
    private static final long TIMEOUT_SECONDS = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        
        mAuth = FirebaseAuth.getInstance();
        
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        tvStatus = findViewById(R.id.tvStatus);
        
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnVerify.setOnClickListener(v -> verifyCode());
        
        // Если пользователь уже вошёл — переходим в главное меню
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    
    private void sendVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Введите номер телефона");
            return;
        }
        
        btnSendCode.setEnabled(false);
        tvStatus.setText("Отправка кода...");
        
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        tvStatus.setText("Автоматическая проверка...");
                        signInWithPhoneAuthCredential(credential);
                    }
                    
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        tvStatus.setText("Ошибка: " + e.getMessage());
                        btnSendCode.setEnabled(true);
                        Toast.makeText(PhoneVerificationActivity.this, 
                            "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    
                    @Override
                    public void onCodeSent(@NonNull String vid, 
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = vid;
                        resendToken = token;
                        tvStatus.setText("Код отправлен. Введите код из SMS");
                        btnSendCode.setEnabled(false);
                        btnVerify.setEnabled(true);
                    }
                    
                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String vid) {
                        tvStatus.setText("Время автоматической проверки истекло. Введите код вручную");
                    }
                })
                .build();
        
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    
    private void verifyCode() {
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Введите код");
            return;
        }
        
        tvStatus.setText("Проверка кода...");
        btnVerify.setEnabled(false);
        
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    tvStatus.setText("Вход выполнен!");
                    Toast.makeText(this, "Успешный вход", Toast.LENGTH_SHORT).show();
                    
                    // Переход в главное меню
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    tvStatus.setText("Ошибка входа");
                    btnVerify.setEnabled(true);
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ошибка: " + task.getException().getMessage(), 
                            Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
}
