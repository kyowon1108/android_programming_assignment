package com.example.babydiary.service;

import android.content.Context;
import android.util.Log;

import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.Tag;
import com.example.babydiary.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 태그 관련 API 서비스
 */
public class TagService {
    private static final String TAG = "TagService";
    private final Gson gson = new Gson();

    /**
     * 모든 태그 조회
     * @param listener 응답 리스너
     */
    public void getAllTags(OnApiResponseListener<List<Tag>> listener) {
        ApiClient.get(Constants.ENDPOINT_TAGS, null, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type listType = new TypeToken<List<Tag>>(){}.getType();
                    List<Tag> tags = gson.fromJson(response, listType);
                    listener.onSuccess(tags);
                } catch (Exception e) {
                    Log.e(TAG, "Get tags parsing error: " + e.getMessage());
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
     * 태그 카테고리 목록 조회
     * @param listener 응답 리스너
     */
    public void getTagCategories(OnApiResponseListener<List<String>> listener) {
        ApiClient.get(Constants.ENDPOINT_TAG_CATEGORIES, null, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type listType = new TypeToken<List<String>>(){}.getType();
                    List<String> categories = gson.fromJson(response, listType);
                    listener.onSuccess(categories);
                } catch (Exception e) {
                    Log.e(TAG, "Get categories parsing error: " + e.getMessage());
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
     * 카테고리별 태그 조회
     * @param category 카테고리명
     * @param listener 응답 리스너
     */
    public void getTagsByCategory(String category, OnApiResponseListener<List<Tag>> listener) {
        String endpoint = String.format(Constants.ENDPOINT_TAG_BY_CATEGORY, category);

        ApiClient.get(endpoint, null, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type listType = new TypeToken<List<Tag>>(){}.getType();
                    List<Tag> tags = gson.fromJson(response, listType);
                    listener.onSuccess(tags);
                } catch (Exception e) {
                    Log.e(TAG, "Get tags by category parsing error: " + e.getMessage());
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
