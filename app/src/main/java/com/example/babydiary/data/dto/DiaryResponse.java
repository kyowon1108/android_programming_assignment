package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DiaryResponse {
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

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("tags")
    private List<Tag> tags;

    // Default constructor
    public DiaryResponse() {
    }

    // Getters and Setters
    public int getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVisionDescription() {
        return visionDescription;
    }

    public void setVisionDescription(String visionDescription) {
        this.visionDescription = visionDescription;
    }

    public String getGeneratedStory() {
        return generatedStory;
    }

    public void setGeneratedStory(String generatedStory) {
        this.generatedStory = generatedStory;
    }

    public String getExpertComment() {
        return expertComment;
    }

    public void setExpertComment(String expertComment) {
        this.expertComment = expertComment;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}