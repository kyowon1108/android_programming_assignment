package com.example.babydiary.model;

import com.google.gson.annotations.SerializedName;

/**
 * API 응답 공통 모델
 * @param <T> 데이터 타입 (User, Diary 등)
 */
public class ApiResponse<T> {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("user")
    private User user;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    @SerializedName("detail")
    private String detail;

    private T data;

    // Constructor
    public ApiResponse() {
    }

    // Getters
    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }

    public T getData() {
        return data;
    }

    // Setters
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 응답이 성공인지 확인
     */
    public boolean isSuccess() {
        return error == null || error.isEmpty();
    }

    /**
     * 에러 메시지 반환
     */
    public String getErrorMessage() {
        if (error != null && !error.isEmpty()) {
            return detail != null ? error + ": " + detail : error;
        }
        return null;
    }
}
