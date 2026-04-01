package com.example.messenger.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.messenger.ProfileEditActivity;
import com.example.messenger.R;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.utils.PreferencesManager;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView tvDisplayName, tvUsername, tvBio, tvBirthday;
    private Button btnEditProfile;
    private PreferencesManager prefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        prefManager = new PreferencesManager(getContext());
        
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        
        ivAvatar.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfileEditActivity.class));
        });
        
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfileEditActivity.class));
        });
        
        loadProfile();
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }
    
    private void loadProfile() {
        String token = prefManager.getToken();
        if (token == null) return;
        
        ApiClient.getApi().getProfile("Bearer " + token).enqueue(new Callback<ApiService.ProfileResponse>() {
            @Override
            public void onResponse(Call<ApiService.ProfileResponse> call, Response<ApiService.ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ProfileResponse profile = response.body();
                    tvDisplayName.setText(profile.displayName != null ? profile.displayName : profile.username);
                    tvUsername.setText("@" + profile.username);
                    tvBio.setText(profile.bio != null && !profile.bio.isEmpty() ? profile.bio : "Добавьте описание");
                    tvBirthday.setText(profile.birthday != null && !profile.birthday.isEmpty() ? profile.birthday : "Добавьте дату рождения");
                    
                    if (profile.avatar != null && !profile.avatar.isEmpty()) {
                        Glide.with(ProfileFragment.this)
                                .load(Uri.parse(profile.avatar))
                                .placeholder(R.drawable.ic_default_avatar)
                                .into(ivAvatar);
                    }
                    
                    prefManager.saveDisplayName(profile.displayName);
                    prefManager.saveUsername(profile.username);
                    prefManager.saveBio(profile.bio);
                    prefManager.saveBirthday(profile.birthday);
                    if (profile.avatar != null) prefManager.saveAvatarUri(profile.avatar);
                }
            }
            @Override
            public void onFailure(Call<ApiService.ProfileResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
