package com.example.messenger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.messenger.LoginActivity;
import com.example.messenger.R;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.utils.PreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private PreferencesManager prefManager;
    private Switch swNotifications, swSound, swTheme;
    private TextView tvAccount, tvPrivacy, tvAbout, tvLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        prefManager = new PreferencesManager(getContext());
        
        swNotifications = view.findViewById(R.id.swNotifications);
        swSound = view.findViewById(R.id.swSound);
        swTheme = view.findViewById(R.id.swTheme);
        tvAccount = view.findViewById(R.id.tvAccount);
        tvPrivacy = view.findViewById(R.id.tvPrivacy);
        tvAbout = view.findViewById(R.id.tvAbout);
        tvLogout = view.findViewById(R.id.tvLogout);
        
        loadSettings();
        
        swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefManager.saveNotificationsEnabled(isChecked);
            Toast.makeText(getContext(), isChecked ? "Уведомления включены" : "Уведомления выключены", Toast.LENGTH_SHORT).show();
        });
        
        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefManager.saveSoundEnabled(isChecked);
            Toast.makeText(getContext(), isChecked ? "Звук включён" : "Звук выключен", Toast.LENGTH_SHORT).show();
        });
        
        swTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newTheme = isChecked ? "dark" : "light";
            prefManager.saveTheme(newTheme);
            
            ApiService.ProfileRequest req = new ApiService.ProfileRequest();
            req.theme = newTheme;
            ApiClient.getApi().updateProfile("Bearer " + prefManager.getToken(), req).enqueue(new Callback<ApiService.SimpleResponse>() {
                @Override
                public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                }
                @Override
                public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                }
            });
        });
        
        tvAccount.setOnClickListener(v -> {
            showInfoDialog("Аккаунт", 
                "Email: " + prefManager.getEmail() + "\n" +
                "Username: @" + prefManager.getUsername() + "\n" +
                "Имя: " + prefManager.getDisplayName()
            );
        });
        
        tvPrivacy.setOnClickListener(v -> {
            showInfoDialog("Конфиденциальность", 
                "Ваши данные хранятся на сервере и не передаются третьим лицам.\n\n" +
                "• Email используется для входа\n" +
                "• Username виден другим пользователям\n" +
                "• Имя и фото видны в профиле"
            );
        });
        
        tvAbout.setOnClickListener(v -> {
            showInfoDialog("О приложении", 
                "Messenger v1.0\n\n" +
                "Стиль Telegram\n" +
                "Авторизация по email\n" +
                "Обмен сообщениями в реальном времени\n" +
                "Настройка профиля и аватара\n" +
                "Тёмная тема"
            );
        });
        
        tvLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Выход")
                    .setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        prefManager.clear();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });
        
        return view;
    }
    
    private void loadSettings() {
        swNotifications.setChecked(prefManager.isNotificationsEnabled());
        swSound.setChecked(prefManager.isSoundEnabled());
        swTheme.setChecked(prefManager.getTheme().equals("dark"));
    }
    
    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
