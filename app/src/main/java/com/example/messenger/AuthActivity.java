package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.utils.PreferencesManager;

public class AuthActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private PreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefManager = new PreferencesManager(this);
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Если уже залогинен — сразу в главное меню
        if (prefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void applyTheme() {
        String theme = prefManager.getTheme();
        if (theme.equals("dark")) {
            setTheme(R.style.Theme_Messenger_Dark);
        } else {
            setTheme(R.style.Theme_Messenger);
        }
    }
}
