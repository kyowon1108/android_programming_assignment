package com.example.babydiary.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 주간 다이어리 모델
 */
public class WeeklyDiary {
    @SerializedName("week_id")
    private int weekId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("year")
    private int year;

    @SerializedName("week_number")
    private int weekNumber;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("weekly_summary_text")
    private String weeklySummaryText;

    @SerializedName("weekly_image_url")
    private String weeklyImageUrl;

    @SerializedName("weekly_title")
    private String weeklyTitle;

    @SerializedName("user_uploaded_image")
    private boolean userUploadedImage;

    @SerializedName("diaries")
    private List<Diary> diaries;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Constructor
    public WeeklyDiary() {
    }

    // Getters
    public int getWeekId() {
        return weekId;
    }

    public int getUserId() {
        return userId;
    }

    public int getYear() {
        return year;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getWeeklySummaryText() {
        return weeklySummaryText;
    }

    public String getWeeklyImageUrl() {
        return weeklyImageUrl;
    }

    public String getWeeklyTitle() {
        return weeklyTitle;
    }

    public boolean isUserUploadedImage() {
        return userUploadedImage;
    }

    public List<Diary> getDiaries() {
        return diaries;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setWeeklySummaryText(String weeklySummaryText) {
        this.weeklySummaryText = weeklySummaryText;
    }

    public void setWeeklyImageUrl(String weeklyImageUrl) {
        this.weeklyImageUrl = weeklyImageUrl;
    }

    public void setWeeklyTitle(String weeklyTitle) {
        this.weeklyTitle = weeklyTitle;
    }

    public void setUserUploadedImage(boolean userUploadedImage) {
        this.userUploadedImage = userUploadedImage;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
