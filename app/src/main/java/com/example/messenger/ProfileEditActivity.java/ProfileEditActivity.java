package com.example.messenger;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.messenger.utils.PreferencesManager;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Calendar;

public class ProfileEditActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private EditText etUsername, etBio, etBirthday;
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
        String newUsername = etUsername.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username не может быть пустым", Toast.LENGTH_SHORT).show();
            return;
        }

        prefManager.saveUsername(newUsername);
        prefManager.saveBio(bio);
        prefManager.saveBirthday(birthday);

        if (selectedImageUri != null) {
            prefManager.saveAvatarUri(selectedImageUri.toString());
        }

        Toast.makeText(this, "Профиль сохранён", Toast.LENGTH_SHORT).show();
        finish();
    }
}
