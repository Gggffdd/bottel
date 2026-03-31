package com.example.messenger;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.messenger.network.ApiClient;
import com.example.messenger.network.ApiService;
import com.example.messenger.utils.PreferencesManager;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private EditText etDisplayName, etUsername, etBio, etBirthday;
    private Button btnSave;
    private PreferencesManager prefManager;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(ivAvatar);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        prefManager = new PreferencesManager(this);

        ivAvatar = findViewById(R.id.ivAvatar);
        etDisplayName = findViewById(R.id.etDisplayName);
        etUsername = findViewById(R.id.etUsername);
        etBio = findViewById(R.id.etBio);
        etBirthday = findViewById(R.id.etBirthday);
        btnSave = findViewById(R.id.btnSave);

        loadCurrentData();

        ivAvatar.setOnClickListener(v -> openImagePicker());
        etBirthday.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentData() {
        etDisplayName.setText(prefManager.getDisplayName());
        etUsername.setText(prefManager.getUsername());
        etBio.setText(prefManager.getBio());
        etBirthday.setText(prefManager.getBirthday());

        String avatarUri = prefManager.getAvatarUri();
        if (!avatarUri.isEmpty()) {
            Glide.with(this).load(Uri.parse(avatarUri)).into(ivAvatar);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "." + (month + 1) + "." + year;
                    etBirthday.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveProfile() {
        String displayName = etDisplayName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();

        if (displayName.isEmpty()) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getApi();
        String token = "Bearer " + prefManager.getToken();
        
        // Обновляем username и displayName
        ApiService.ProfileRequest request = new ApiService.ProfileRequest();
        request.displayName = displayName;
        request.username = username;
        request.bio = bio;
        request.birthday = birthday;
        if (selectedImageUri != null) {
            request.avatar = selectedImageUri.toString();
        }

        api.updateProfile(token, request).enqueue(new Callback<ApiService.SimpleResponse>() {
            @Override
            public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Обновляем локальные данные
                    prefManager.saveDisplayName(displayName);
                    if (!username.isEmpty()) prefManager.saveUsername(username);
                    prefManager.saveBio(bio);
                    prefManager.saveBirthday(birthday);
                    if (selectedImageUri != null) prefManager.saveAvatarUri(selectedImageUri.toString());
                    
                    Toast.makeText(ProfileEditActivity.this, "Профиль сохранён", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String msg = response.body() != null && response.body().error != null ? response.body().error : "Ошибка сохранения";
                    Toast.makeText(ProfileEditActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                Toast.makeText(ProfileEditActivity.this, "Сетевая ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
