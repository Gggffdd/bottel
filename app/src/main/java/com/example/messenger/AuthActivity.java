package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.utils.PreferencesManager;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        PreferencesManager prefManager = new PreferencesManager(this);
        
        // Проверяем токен
        Log.d(TAG, "Token in AuthActivity: " + prefManager.getToken());
        Log.d(TAG, "isLoggedIn in AuthActivity: " + prefManager.isLoggedIn());
        
        // Если токен есть — идём в MainActivity, иначе — на вход
        if (prefManager.isLoggedIn()) {
            Log.d(TAG, "Token found, going to MainActivity");
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Log.d(TAG, "No token, going to EmailVerificationActivity");
            startActivity(new Intent(this, EmailVerificationActivity.class));
        }
        finish();
    }
}
