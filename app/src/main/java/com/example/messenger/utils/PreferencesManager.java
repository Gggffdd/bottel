package com.example.messenger.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "messenger";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_BIO = "bio";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_AVATAR_URI = "avatar_uri";
    private static final String KEY_THEME = "theme";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_SOUND = "sound";

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ========== ОСНОВНЫЕ МЕТОДЫ ==========

    public void saveUser(String email, String token) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_TOKEN, token)
            .apply();
    }

    public void saveUserData(String email, String username, String displayName, String bio, String birthday, String avatar, String theme) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_USERNAME, username)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_BIO, bio)
            .putString(KEY_BIRTHDAY, birthday)
            .putString(KEY_AVATAR_URI, avatar)
            .putString(KEY_THEME, theme)
            .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    // ========== USERNAME / DISPLAY NAME ==========

    public void saveUsername(String username) {
        prefs.edit().putString(KEY_USERNAME, username).apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public void saveDisplayName(String name) {
        prefs.edit().putString(KEY_DISPLAY_NAME, name).apply();
    }

    public String getDisplayName() {
        return prefs.getString(KEY_DISPLAY_NAME, getUsername());
    }

    // ========== ПРОФИЛЬ ==========

    public void saveBio(String bio) {
        prefs.edit().putString(KEY_BIO, bio).apply();
    }

    public String getBio() {
        return prefs.getString(KEY_BIO, "");
    }

    public void saveBirthday(String birthday) {
        prefs.edit().putString(KEY_BIRTHDAY, birthday).apply();
    }

    public String getBirthday() {
        return prefs.getString(KEY_BIRTHDAY, "");
    }

    public void saveAvatarUri(String uri) {
        prefs.edit().putString(KEY_AVATAR_URI, uri).apply();
    }

    public String getAvatarUri() {
        return prefs.getString(KEY_AVATAR_URI, "");
    }

    // ========== ТЕМА ==========

    public void saveTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, "light");
    }

    // ========== НАСТРОЙКИ ==========

    public void saveNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void saveSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND, true);
    }
}
