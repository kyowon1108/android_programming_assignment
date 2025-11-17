package com.example.babydiary.data.api;

import com.example.babydiary.data.dto.UpdateNicknameRequest;
import com.example.babydiary.data.dto.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {

    @GET("users/profile")
    Call<UserProfileResponse> getProfile();

    @PUT("users/profile")
    Call<UserProfileResponse> updateProfile(@Body UpdateNicknameRequest request);
}