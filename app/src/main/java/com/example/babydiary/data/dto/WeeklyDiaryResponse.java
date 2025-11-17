package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;

public class WeeklyDiaryResponse {
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

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Default constructor
    public WeeklyDiaryResponse() {
    }

    // Getters and Setters
    public int getWeekId() {
        return weekId;
    }

    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWeeklySummaryText() {
        return weeklySummaryText;
    }

    public void setWeeklySummaryText(String weeklySummaryText) {
        this.weeklySummaryText = weeklySummaryText;
    }

    public String getWeeklyImageUrl() {
        return weeklyImageUrl;
    }

    public void setWeeklyImageUrl(String weeklyImageUrl) {
        this.weeklyImageUrl = weeklyImageUrl;
    }

    public String getWeeklyTitle() {
        return weeklyTitle;
    }

    public void setWeeklyTitle(String weeklyTitle) {
        this.weeklyTitle = weeklyTitle;
    }

    public boolean isUserUploadedImage() {
        return userUploadedImage;
    }

    public void setUserUploadedImage(boolean userUploadedImage) {
        this.userUploadedImage = userUploadedImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}