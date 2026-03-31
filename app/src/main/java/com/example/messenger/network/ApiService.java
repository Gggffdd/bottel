package com.example.messenger.network;

import com.example.messenger.models.Message;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    // Регистрация и логин (остаются для обратной совместимости)
    @POST("api/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Поиск пользователей
    @GET("api/users/search")
    Call<List<UserSearchResult>> searchUsers(@Header("Authorization") String auth, @Query("q") String query);

    // Сообщения
    @GET("api/messages/{withUser}")
    Call<List<Message>> getMessages(@Header("Authorization") String auth, @Path("withUser") String withUser);

    // Профиль
    @GET("api/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String auth);

    @POST("api/profile")
    Call<SimpleResponse> updateProfile(@Header("Authorization") String auth, @Body ProfileRequest request);

    // ========== НОВЫЕ МЕТОДЫ ДЛЯ EMAIL-ВЕРИФИКАЦИИ ==========
    @POST("api/send-code")
    Call<SimpleResponse> sendCode(@Body SendCodeRequest request);

    @POST("api/verify-code")
    Call<VerifyCodeResponse> verifyCode(@Body VerifyCodeRequest request);

    // ========== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ==========

    class LoginRequest {
        public String phone;
        public String password;
    }

    class RegisterRequest {
        public String phone;
        public String password;
        public String username;
    }

    class LoginResponse {
        public boolean success;
        public String token;
        public String username;
        public String displayName;
        public String error;
    }

    class ProfileRequest {
        public String displayName;
        public String username;
        public String bio;
        public String birthday;
        public String avatar;
    }

    class ProfileResponse {
        public String email;
        public String username;
        public String displayName;
        public String bio;
        public String birthday;
        public String avatar;
    }

    class UserSearchResult {
        public String username;
        public String displayName;
    }

    class SimpleResponse {
        public boolean success;
        public String message;
        public String error;
    }

    // ========== НОВЫЕ КЛАССЫ ДЛЯ EMAIL-ВЕРИФИКАЦИИ ==========

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
        public String error;
    }
}
