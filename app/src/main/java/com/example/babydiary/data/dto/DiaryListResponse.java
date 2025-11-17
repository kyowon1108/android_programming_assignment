package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DiaryListResponse {
    @SerializedName("total")
    private int total;

    @SerializedName("diaries")
    private List<DiaryResponse> diaries;

    // Default constructor
    public DiaryListResponse() {
    }

    // Constructor with all fields
    public DiaryListResponse(int total, List<DiaryResponse> diaries) {
        this.total = total;
        this.diaries = diaries;
    }

    // Getters and Setters
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DiaryResponse> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<DiaryResponse> diaries) {
        this.diaries = diaries;
    }
}