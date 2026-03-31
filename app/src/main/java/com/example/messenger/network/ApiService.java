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
    Call<List<String>> searchUsers(@Header("Authorization") String auth, @Query("q") String query);

    @GET("api/messages/{withUser}")
    Call<List<Message>> getMessages(@Header("Authorization") String auth, @Path("withUser") String withUser);

    class LoginRequest { public String phone; public String password; }
    class RegisterRequest { public String phone; public String username; public String password; }
    class LoginResponse { public boolean success; public String token; public String username; public String error; }
}
