package com.example.babydiary.model;

import com.google.gson.annotations.SerializedName;

/**
 * 태그 모델
 */
public class Tag {
    @SerializedName("tag_id")
    private int tagId;

    @SerializedName("tag_name")
    private String tagName;

    @SerializedName("tag_category")
    private String tagCategory;

    // Constructor
    public Tag() {
    }

    public Tag(int tagId, String tagName, String tagCategory) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagCategory = tagCategory;
    }

    // Getters
    public int getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getName() {
        return tagName;
    }

    public String getTagCategory() {
        return tagCategory;
    }

    // Setters
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setTagCategory(String tagCategory) {
        this.tagCategory = tagCategory;
    }
}
