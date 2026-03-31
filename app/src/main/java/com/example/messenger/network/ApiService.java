package com.example.messenger.network;

import com.example.messenger.models.Message;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    // ========== АВТОРИЗАЦИЯ И РЕГИСТРАЦИЯ ==========
    
    // Регистрация (номер + email + пароль)
    @POST("api/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    // Логин (email + пароль) — старый способ, для совместимости
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ========== EMAIL ВЕРИФИКАЦИЯ ==========
    
    // Отправка кода на email
    @POST("api/send-code")
    Call<SimpleResponse> sendCode(@Body SendCodeRequest request);

    // Проверка кода
    @POST("api/verify-code")
    Call<VerifyCodeResponse> verifyCode(@Body VerifyCodeRequest request);

    // ========== ПОИСК ПОЛЬЗОВАТЕЛЕЙ ==========
    
    @GET("api/users/search")
    Call<List<UserSearchResult>> searchUsers(@Header("Authorization") String auth, @Query("q") String query);

    // ========== СООБЩЕНИЯ ==========
    
    @GET("api/messages/{withUser}")
    Call<List<Message>> getMessages(@Header("Authorization") String auth, @Path("withUser") String withUser);

    // ========== ПРОФИЛЬ ==========
    
    @GET("api/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String auth);

    @POST("api/profile")
    Call<SimpleResponse> updateProfile(@Header("Authorization") String auth, @Body ProfileRequest request);

    // ========== ПРОВЕРКА USERNAME ==========
    
    @POST("api/check-username")
    Call<UsernameCheckResponse> checkUsername(@Header("Authorization") String auth, @Body UsernameCheckRequest request);

    // ========== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ==========

    // Регистрация
    class RegisterRequest {
        public String phone;
        public String email;
        public String password;
    }

    // Логин
    class LoginRequest {
        public String email;
        public String password;
    }

    // Ответ на логин/регистрацию
    class LoginResponse {
        public boolean success;
        public String token;
        public String username;
        public String displayName;
        public String error;
    }

    // Профиль
    class ProfileRequest {
        public String displayName;
        public String username;
        public String bio;
        public String birthday;
        public String avatar;
        public String theme;
    }

    class ProfileResponse {
        public String email;
        public String username;
        public String displayName;
        public String bio;
        public String birthday;
        public String avatar;
        public String theme;
    }

    // Результат поиска пользователя
    class UserSearchResult {
        public String username;
        public String displayName;
    }

    // Простой ответ (успех/ошибка)
    class SimpleResponse {
        public boolean success;
        public String message;
        public String error;
    }

    // ========== EMAIL ВЕРИФИКАЦИЯ ==========

    class SendCodeRequest {
        public String email;
    }

    class VerifyCodeRequest {
        public String email;
        public String code;
    }

    class VerifyCodeResponse {
        public boolean success;
        public String token;
        public boolean isNew;
        public UserData user;
        public String error;
    }

    class UserData {
        public String email;
        public String username;
        public String displayName;
        public String bio;
        public String birthday;
        public String avatar;
        public String theme;
    }

    // ========== ПРОВЕРКА USERNAME ==========

    class UsernameCheckRequest {
        public String username;
    }

    class UsernameCheckResponse {
        public boolean available;
        public String message;
    }
}
