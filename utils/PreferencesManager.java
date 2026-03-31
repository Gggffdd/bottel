package com.example.messenger.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "messenger";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String username, String token) {
        prefs.edit().putString(KEY_USERNAME, username).putString(KEY_TOKEN, token).apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, null); }
    public boolean isLoggedIn() { return getToken() != null; }
    public void clear() { prefs.edit().clear().apply(); }
}
