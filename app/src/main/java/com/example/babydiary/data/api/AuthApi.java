package com.example.babydiary.data.api;

import com.example.babydiary.data.dto.LoginRequest;
import com.example.babydiary.data.dto.LoginResponse;
import com.example.babydiary.data.dto.MessageResponse;
import com.example.babydiary.data.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/register")
    Call<MessageResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<MessageResponse> logout();
}