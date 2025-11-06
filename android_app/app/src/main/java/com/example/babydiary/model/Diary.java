package com.example.babydiary.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 다이어리 모델
 */
public class Diary {
    @SerializedName("diary_id")
    private int diaryId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("date")
    private String date;

    @SerializedName("description")
    private String description;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("vision_description")
    private String visionDescription;

    @SerializedName("generated_story")
    private String generatedStory;

    @SerializedName("expert_comment")
    private String expertComment;

    @SerializedName("emotion")
    private String emotion;

    @SerializedName("year")
    private int year;

    @SerializedName("week_number")
    private int weekNumber;

    @SerializedName("tags")
    private List<Tag> tags;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Constructor
    public Diary() {
    }

    // Getters
    public int getDiaryId() {
        return diaryId;
    }

    public int getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getVisionDescription() {
        return visionDescription;
    }

    public String getGeneratedStory() {
        return generatedStory;
    }

    public String getExpertComment() {
        return expertComment;
    }

    public String getEmotion() {
        return emotion;
    }

    public int getYear() {
        return year;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setVisionDescription(String visionDescription) {
        this.visionDescription = visionDescription;
    }

    public void setGeneratedStory(String generatedStory) {
        this.generatedStory = generatedStory;
    }

    public void setExpertComment(String expertComment) {
        this.expertComment = expertComment;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 전체 사진 URL 반환 (BASE_URL + photo_url)
     */
    public String getFullPhotoUrl() {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return null;
        }
        return com.example.babydiary.util.Constants.BASE_URL + "/uploads/" + photoUrl;
    }
}
