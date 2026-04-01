package com.example.messenger;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messenger.utils.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Просто показываем текст
        TextView tv = new TextView(this);
        tv.setText("MainActivity работает!\n\nEmail: " + new PreferencesManager(this).getEmail());
        tv.setTextSize(20);
        tv.setPadding(40, 40, 40, 40);
        setContentView(tv);
    }
}
