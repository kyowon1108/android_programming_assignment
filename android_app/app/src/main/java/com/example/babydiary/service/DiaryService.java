package com.example.babydiary.service;

import android.content.Context;
import android.util.Log;

import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.util.Constants;
import com.example.babydiary.util.SharedPrefsManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 다이어리 관련 API 서비스
 */
public class DiaryService {
    private static final String TAG = "DiaryService";
    private final Gson gson = new Gson();

    /**
     * 다이어리 생성 (파일 업로드)
     * @param context Context
     * @param description 다이어리 내용
     * @param photoFile 사진 파일
     * @param listener 응답 리스너
     */
    public void createDiary(Context context, String description, File photoFile, OnApiResponseListener<Diary> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        // 텍스트 필드 설정
        Map<String, String> textFields = new HashMap<>();
        textFields.put("description", description);

        // Multipart 업로드
        ApiClient.postMultipart(Constants.ENDPOINT_DIARIES, token, photoFile, "photo", textFields, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Diary diary = gson.fromJson(response, Diary.class);
                    listener.onSuccess(diary);
                } catch (Exception e) {
                    Log.e(TAG, "Create diary parsing error: " + e.getMessage());
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
     * 다이어리 목록 조회
     * @param context Context
     * @param page 페이지 번호
     * @param pageSize 페이지 크기
     * @param listener 응답 리스너
     */
    public void getDiaries(Context context, int page, int pageSize, OnApiResponseListener<List<Diary>> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        String endpoint = Constants.ENDPOINT_DIARIES + "?page=" + page + "&page_size=" + pageSize;

        ApiClient.get(endpoint, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    // DiaryListResponse 파싱
                    DiaryListResponse listResponse = gson.fromJson(response, DiaryListResponse.class);
                    listener.onSuccess(listResponse.diaries);
                } catch (Exception e) {
                    Log.e(TAG, "Get diaries parsing error: " + e.getMessage());
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
     * 다이어리 상세 조회
     * @param context Context
     * @param diaryId 다이어리 ID
     * @param listener 응답 리스너
     */
    public void getDiary(Context context, int diaryId, OnApiResponseListener<Diary> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        String endpoint = String.format(Constants.ENDPOINT_DIARY_BY_ID, diaryId);

        ApiClient.get(endpoint, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Diary diary = gson.fromJson(response, Diary.class);
                    listener.onSuccess(diary);
                } catch (Exception e) {
                    Log.e(TAG, "Get diary parsing error: " + e.getMessage());
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
     * 다이어리 삭제
     * @param context Context
     * @param diaryId 다이어리 ID
     * @param listener 응답 리스너
     */
    public void deleteDiary(Context context, int diaryId, OnApiResponseListener<Boolean> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        String endpoint = String.format(Constants.ENDPOINT_DIARY_BY_ID, diaryId);

        ApiClient.delete(endpoint, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                listener.onSuccess(true);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }

    /**
     * 다이어리 목록 응답 모델
     */
    private static class DiaryListResponse {
        List<Diary> diaries;
        int total;
        int page;
        int page_size;
    }
}
