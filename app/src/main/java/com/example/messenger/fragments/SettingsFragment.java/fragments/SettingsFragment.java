package com.example.messenger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.messenger.LoginActivity;
import com.example.messenger.R;
import com.example.messenger.utils.PreferencesManager;

public class SettingsFragment extends Fragment {

    private PreferencesManager prefManager;
    private Switch swNotifications, swSound;
    private TextView tvAccount, tvPrivacy, tvAbout, tvLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        prefManager = new PreferencesManager(getContext());
        
        swNotifications = view.findViewById(R.id.swNotifications);
        swSound = view.findViewById(R.id.swSound);
        tvAccount = view.findViewById(R.id.tvAccount);
        tvPrivacy = view.findViewById(R.id.tvPrivacy);
        tvAbout = view.findViewById(R.id.tvAbout);
        tvLogout = view.findViewById(R.id.tvLogout);
        
        loadSettings();
        
        swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefManager.saveNotificationsEnabled(isChecked);
        });
        
        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefManager.saveSoundEnabled(isChecked);
        });
        
        tvAccount.setOnClickListener(v -> {
            showInfoDialog("Аккаунт", "Управление аккаунтом: " + prefManager.getUsername());
        });
        
        tvPrivacy.setOnClickListener(v -> {
            showInfoDialog("Конфиденциальность", "Ваши данные хранятся на сервере и не передаются третьим лицам.");
        });
        
        tvAbout.setOnClickListener(v -> {
            showInfoDialog("О приложении", "Messenger v1.0\nСтиль Telegram\nСделки и репутация");
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
    }
    
    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
