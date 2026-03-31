package com.example.messenger.network;

import com.example.messenger.models.Message;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {
    @POST("api/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/users/search")
    Call<List<UserSearchResult>> searchUsers(@Header("Authorization") String auth, @Query("q") String query);

    @GET("api/messages/{withUser}")
    Call<List<Message>> getMessages(@Header("Authorization") String auth, @Path("withUser") String withUser);

    @GET("api/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String auth);

    @POST("api/profile")
    Call<SimpleResponse> updateProfile(@Header("Authorization") String auth, @Body ProfileRequest request);

    class LoginRequest {
        public String phone;
        public String password;
    }

    class RegisterRequest {
        public String phone;
        public String password;
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
        public String error;
    }
}
