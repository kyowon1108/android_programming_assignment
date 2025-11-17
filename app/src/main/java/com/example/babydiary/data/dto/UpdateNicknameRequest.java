package com.example.babydiary.data.dto;

import com.google.gson.annotations.SerializedName;

public class UpdateNicknameRequest {
    @SerializedName("nickname")
    private String nickname;

    public UpdateNicknameRequest(String nickname) {
        this.nickname = nickname;
    }

    // Getters and Setters
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}