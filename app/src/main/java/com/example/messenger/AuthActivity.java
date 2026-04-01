package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.utils.PreferencesManager;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PreferencesManager prefManager = new PreferencesManager(this);
        
        // Временно показываем статус
        TextView tv = new TextView(this);
        tv.setText("AuthActivity\n\nToken: " + prefManager.getToken() + "\n\nПереход через 2 секунды...");
        tv.setTextSize(16);
        tv.setPadding(40, 40, 40, 40);
        setContentView(tv);
        
        // Задержка для наглядности
        new android.os.Handler().postDelayed(() -> {
            if (prefManager.isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, EmailVerificationActivity.class));
            }
            finish();
        }, 2000);
    }
}
