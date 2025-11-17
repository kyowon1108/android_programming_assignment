package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("tag_id")
    private int tagId;

    @SerializedName("tag_name")
    private String tagName;

    @SerializedName("tag_category")
    private String tagCategory;

    @SerializedName("created_at")
    private String createdAt;

    // Default constructor
    public Tag() {
    }

    // Constructor with all fields
    public Tag(int tagId, String tagName, String tagCategory, String createdAt) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagCategory = tagCategory;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagCategory() {
        return tagCategory;
    }

    public void setTagCategory(String tagCategory) {
        this.tagCategory = tagCategory;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}