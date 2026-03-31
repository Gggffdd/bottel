package com.example.messenger.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.messenger.R;
import com.example.messenger.ProfileEditActivity;
import com.example.messenger.utils.PreferencesManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView tvUsername, tvBio, tvBirthday;
    private Button btnEditProfile;
    private PreferencesManager prefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        prefManager = new PreferencesManager(getContext());
        
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        
        loadProfile();
        
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfileEditActivity.class));
        });
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }
    
    private void loadProfile() {
        String username = prefManager.getUsername();
        String bio = prefManager.getBio();
        String birthday = prefManager.getBirthday();
        String avatarUri = prefManager.getAvatarUri();
        
        tvUsername.setText("@" + username);
        tvBio.setText(bio.isEmpty() ? "Добавьте описание" : bio);
        tvBirthday.setText(birthday.isEmpty() ? "Добавьте дату рождения" : birthday);
        
        if (!avatarUri.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(avatarUri))
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(ivAvatar);
        }
    }
}
