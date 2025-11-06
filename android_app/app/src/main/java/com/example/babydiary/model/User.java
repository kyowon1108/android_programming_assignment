package com.example.babydiary.model;

import com.example.babydiary.util.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * 사용자 정보 모델
 */
public class User {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("email")
    private String email;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    @SerializedName("best_streak")
    private int bestStreak;

    @SerializedName("current_streak")
    private int currentStreak;

    @SerializedName("last_diary_date")
    private String lastDiaryDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Constructor
    public User() {
    }

    public User(int userId, String email, String nickname) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public String getLastDiaryDate() {
        return lastDiaryDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 전체 프로필 이미지 URL 반환
     * @return 서버 BASE_URL + profileImageUrl
     */
    public String getFullProfileImageUrl() {
        if (profileImageUrl == null || profileImageUrl.isEmpty()) {
            return null;
        }
        return Constants.BASE_URL + "/uploads/" + profileImageUrl;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public void setLastDiaryDate(String lastDiaryDate) {
        this.lastDiaryDate = lastDiaryDate;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
