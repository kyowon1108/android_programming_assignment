package com.example.babydiary.service;

import android.content.Context;
import android.util.Log;

import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.ApiResponse;
import com.example.babydiary.model.User;
import com.example.babydiary.util.Constants;
import com.example.babydiary.util.SharedPrefsManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 인증 관련 API 서비스
 */
public class AuthService {
    private static final String TAG = "AuthService";
    private final Gson gson = new Gson();

    /**
     * 회원가입
     * @param email 이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @param listener 응답 리스너
     */
    public void register(String email, String password, String nickname, OnApiResponseListener<User> listener) {
        // JSON 요청 바디 생성
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("email", email);
        jsonBody.addProperty("password", password);
        jsonBody.addProperty("nickname", nickname);

        ApiClient.post(Constants.ENDPOINT_REGISTER, null, jsonBody.toString(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ApiResponse<User> apiResponse = gson.fromJson(response, ApiResponse.class);
                    if (apiResponse.isSuccess()) {
                        listener.onSuccess(apiResponse.getUser());
                    } else {
                        listener.onError(apiResponse.getErrorMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Register parsing error: " + e.getMessage());
                    listener.onError("응답 파싱 오류");
                }
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }

    /**
     * 로그인
     * @param context Context
     * @param email 이메일
     * @param password 비밀번호
     * @param listener 응답 리스너
     */
    public void login(Context context, String email, String password, OnApiResponseListener<User> listener) {
        // JSON 요청 바디 생성
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("email", email);
        jsonBody.addProperty("password", password);

        ApiClient.post(Constants.ENDPOINT_LOGIN, null, jsonBody.toString(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ApiResponse<User> apiResponse = gson.fromJson(response, ApiResponse.class);
                    if (apiResponse.isSuccess() && apiResponse.getAccessToken() != null) {
                        // 토큰 저장
                        SharedPrefsManager.saveToken(context, apiResponse.getAccessToken());

                        // 사용자 정보 저장
                        User user = apiResponse.getUser();
                        if (user != null) {
                            SharedPrefsManager.saveUserId(context, user.getUserId());
                            SharedPrefsManager.saveEmail(context, user.getEmail());
                            SharedPrefsManager.saveNickname(context, user.getNickname());
                        }

                        listener.onSuccess(user);
                    } else {
                        listener.onError(apiResponse.getErrorMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Login parsing error: " + e.getMessage());
                    listener.onError("응답 파싱 오류");
                }
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }

    /**
     * 현재 사용자 정보 조회
     * @param context Context
     * @param listener 응답 리스너
     */
    public void getCurrentUser(Context context, OnApiResponseListener<User> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        ApiClient.get(Constants.ENDPOINT_ME, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    User user = gson.fromJson(response, User.class);
                    listener.onSuccess(user);
                } catch (Exception e) {
                    Log.e(TAG, "Get user parsing error: " + e.getMessage());
                    listener.onError("응답 파싱 오류");
                }
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }

    /**
     * 로그아웃
     * @param context Context
     */
    public void logout(Context context) {
        SharedPrefsManager.clearAll(context);
    }

    /**
     * 로그인 상태 확인
     * @param context Context
     * @return 로그인 여부
     */
    public boolean isLoggedIn(Context context) {
        return SharedPrefsManager.isLoggedIn(context);
    }
}
