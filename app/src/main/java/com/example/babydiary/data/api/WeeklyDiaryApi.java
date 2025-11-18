package com.example.babydiary.data.api;

import com.example.babydiary.data.dto.WeeklyDiaryResponse;
import com.example.babydiary.data.dto.WeeklyDiaryWithDiariesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WeeklyDiaryApi {

    @GET("weekly_diaries")
    Call<WeeklyDiaryResponse> getWeeklyDiary(
            @Query("year") int year,
            @Query("week_number") int weekNumber
    );

    @GET("weekly_diaries/list")
    Call<List<WeeklyDiaryResponse>> getWeeklyDiaryList(@Query("year") int year);

    @GET("weekly_diaries/details")
    Call<WeeklyDiaryWithDiariesResponse> getWeeklyDiaryDetails(
            @Query("year") int year,
            @Query("week_number") int weekNumber
    );

    @POST("weekly_diaries/generate_title")
    Call<WeeklyDiaryResponse> generateWeeklyTitle(
            @Query("year") int year,
            @Query("week_number") int weekNumber
    );
}