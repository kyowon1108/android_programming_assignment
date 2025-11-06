package com.example.babydiary.service;

import android.content.Context;
import android.util.Log;

import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.WeeklyDiary;
import com.example.babydiary.util.Constants;
import com.example.babydiary.util.SharedPrefsManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 주간 다이어리 관련 API 서비스
 */
public class WeeklyDiaryService {
    private static final String TAG = "WeeklyDiaryService";
    private final Gson gson = new Gson();

    /**
     * 주간 다이어리 생성
     * @param context Context
     * @param year 연도
     * @param weekNumber 주차
     * @param listener 응답 리스너
     */
    public void createWeeklyDiary(Context context, int year, int weekNumber, OnApiResponseListener<WeeklyDiary> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        // JSON 요청 바디 생성
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("year", year);
        jsonBody.addProperty("week_number", weekNumber);

        ApiClient.post(Constants.ENDPOINT_WEEKLY_DIARIES, token, jsonBody.toString(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    WeeklyDiary weeklyDiary = gson.fromJson(response, WeeklyDiary.class);
                    listener.onSuccess(weeklyDiary);
                } catch (Exception e) {
                    Log.e(TAG, "Create weekly diary parsing error: " + e.getMessage());
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
     * 주간 다이어리 목록 조회
     * @param context Context
     * @param year 연도 (nullable)
     * @param listener 응답 리스너
     */
    public void getWeeklyDiaries(Context context, Integer year, OnApiResponseListener<List<WeeklyDiary>> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        String endpoint = Constants.ENDPOINT_WEEKLY_DIARIES;
        if (year != null) {
            endpoint += "?year=" + year;
        }

        ApiClient.get(endpoint, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type listType = new TypeToken<List<WeeklyDiary>>(){}.getType();
                    List<WeeklyDiary> weeklyDiaries = gson.fromJson(response, listType);
                    listener.onSuccess(weeklyDiaries);
                } catch (Exception e) {
                    Log.e(TAG, "Get weekly diaries parsing error: " + e.getMessage());
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
     * 주간 다이어리 상세 조회 (일일 다이어리 포함)
     * @param context Context
     * @param weekId 주간 다이어리 ID
     * @param listener 응답 리스너
     */
    public void getWeeklyDiaryDetail(Context context, int weekId, OnApiResponseListener<WeeklyDiary> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        String endpoint = String.format(Constants.ENDPOINT_WEEKLY_DIARY_BY_ID, weekId);

        ApiClient.get(endpoint, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    WeeklyDiary weeklyDiary = gson.fromJson(response, WeeklyDiary.class);
                    listener.onSuccess(weeklyDiary);
                } catch (Exception e) {
                    Log.e(TAG, "Get weekly diary detail parsing error: " + e.getMessage());
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
     * 연도/주차로 주간 다이어리 조회
     * @param context Context
     * @param year 연도
     * @param weekNumber 주차
     * @param listener 응답 리스너
     */
    public void getWeeklyDiaryByDate(Context context, int year, int weekNumber, OnApiResponseListener<WeeklyDiary> listener) {
        String token = SharedPrefsManager.getToken(context);
        if (token == null) {
            listener.onError(Constants.ERROR_UNAUTHORIZED);
            return;
        }

        String endpoint = String.format(Constants.ENDPOINT_WEEKLY_DIARY_BY_DATE, year, weekNumber);

        ApiClient.get(endpoint, token, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    WeeklyDiary weeklyDiary = gson.fromJson(response, WeeklyDiary.class);
                    listener.onSuccess(weeklyDiary);
                } catch (Exception e) {
                    Log.e(TAG, "Get weekly diary by date parsing error: " + e.getMessage());
                    listener.onError("응답 파싱 오류");
                }
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }
}
