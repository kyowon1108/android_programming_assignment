package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeeklyDiaryWithDiariesResponse {
    @SerializedName("weekly_diary")
    private WeeklyDiaryResponse weeklyDiary;

    @SerializedName("diaries")
    private List<DiaryResponse> diaries;

    // Default constructor
    public WeeklyDiaryWithDiariesResponse() {
    }

    // Constructor with all fields
    public WeeklyDiaryWithDiariesResponse(WeeklyDiaryResponse weeklyDiary, List<DiaryResponse> diaries) {
        this.weeklyDiary = weeklyDiary;
        this.diaries = diaries;
    }

    // Getters and Setters
    public WeeklyDiaryResponse getWeeklyDiary() {
        return weeklyDiary;
    }

    public void setWeeklyDiary(WeeklyDiaryResponse weeklyDiary) {
        this.weeklyDiary = weeklyDiary;
    }

    public List<DiaryResponse> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<DiaryResponse> diaries) {
        this.diaries = diaries;
    }
}