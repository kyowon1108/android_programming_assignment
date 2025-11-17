package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {
    @SerializedName("message")
    private String message;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}