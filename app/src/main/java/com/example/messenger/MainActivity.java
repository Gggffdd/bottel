package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.messenger.fragments.ChatsFragment;
import com.example.messenger.fragments.ProfileFragment;
import com.example.messenger.fragments.SettingsFragment;
import com.example.messenger.utils.PreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private PreferencesManager prefManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        prefManager = new PreferencesManager(this);
        
        // Проверяем, залогинен ли пользователь
        if (!prefManager.isLoggedIn()) {
            Log.d(TAG, "User not logged in, redirecting to AuthActivity");
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        
        Log.d(TAG, "User logged in: " + prefManager.getEmail());
        
        setContentView(R.layout.activity_main);
        Log.d(TAG, "setContentView done");
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Log.d(TAG, "bottomNavigationView found: " + (bottomNavigationView != null));
        
        // Диагностический тост
        Toast.makeText(this, "MainActivity запущена. Загружаю Чаты...", Toast.LENGTH_LONG).show();
        
        // Загружаем фрагмент чатов по умолчанию
        loadFragment(new ChatsFragment());
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_chats) {
                selectedFragment = new ChatsFragment();
                Log.d(TAG, "Selected chats");
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                Log.d(TAG, "Selected profile");
            } else if (item.getItemId() == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
                Log.d(TAG, "Selected settings");
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
        
        Log.d(TAG, "onCreate finished");
    }
    
    private void loadFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            Log.d(TAG, "Fragment loaded: " + fragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment", e);
            Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
