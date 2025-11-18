package com.example.babydiary.data.api;

import com.example.babydiary.data.dto.DiaryListResponse;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.dto.MessageResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DiaryApi {

    @Multipart
    @POST("diaries")
    Call<DiaryResponse> createDiary(
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part("tag_ids") RequestBody tagIds,
            @Part MultipartBody.Part photo
    );

    @GET("diaries")
    Call<DiaryListResponse> getDiaries(
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("keyword") String keyword,
            @Query("emotion") String emotion,
            @Query("tag_id") Integer tagId,
            @Query("emotion_tag_id") Integer emotionTagId
    );

    @GET("diaries/{id}")
    Call<DiaryResponse> getDiaryById(@Path("id") int id);

    @DELETE("diaries/{id}")
    Call<MessageResponse> deleteDiary(@Path("id") int id);
}