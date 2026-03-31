package com.example.messenger.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "messenger";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_BIO = "bio";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_AVATAR_URI = "avatar_uri";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";

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

    public void saveDisplayName(String name) { prefs.edit().putString(KEY_DISPLAY_NAME, name).apply(); }
    public String getDisplayName() { return prefs.getString(KEY_DISPLAY_NAME, getUsername()); }
    
    public void saveUsername(String username) { prefs.edit().putString(KEY_USERNAME, username).apply(); }
    
    public void saveBio(String bio) { prefs.edit().putString(KEY_BIO, bio).apply(); }
    public String getBio() { return prefs.getString(KEY_BIO, ""); }
    
    public void saveBirthday(String birthday) { prefs.edit().putString(KEY_BIRTHDAY, birthday).apply(); }
    public String getBirthday() { return prefs.getString(KEY_BIRTHDAY, ""); }
    
    public void saveAvatarUri(String uri) { prefs.edit().putString(KEY_AVATAR_URI, uri).apply(); }
    public String getAvatarUri() { return prefs.getString(KEY_AVATAR_URI, ""); }
    
    public void saveNotificationsEnabled(boolean enabled) { prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply(); }
    public boolean isNotificationsEnabled() { return prefs.getBoolean(KEY_NOTIFICATIONS, true); }
    
    public void saveSoundEnabled(boolean enabled) { prefs.edit().putBoolean(KEY_SOUND, enabled).apply(); }
    public boolean isSoundEnabled() { return prefs.getBoolean(KEY_SOUND, true); }
}
