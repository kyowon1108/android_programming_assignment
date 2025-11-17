package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("email")
    private String email;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    @SerializedName("max_streak")
    private int maxStreak;

    @SerializedName("current_streak")
    private int currentStreak;

    @SerializedName("last_written_date")
    private String lastWrittenDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Default constructor
    public UserProfileResponse() {
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(int maxStreak) {
        this.maxStreak = maxStreak;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public String getLastWrittenDate() {
        return lastWrittenDate;
    }

    public void setLastWrittenDate(String lastWrittenDate) {
        this.lastWrittenDate = lastWrittenDate;
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
