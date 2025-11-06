package com.example.babydiary.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.babydiary.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 통신 기본 클래스
 * HttpsURLConnection을 사용한 API 통신
 */
public class ApiClient {
    private static final String TAG = "ApiClient";

    /**
     * GET 요청
     * @param endpoint API 엔드포인트
     * @param token JWT 토큰 (nullable)
     * @param callback 응답 콜백
     */
    public static void get(String endpoint, String token, ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(Constants.getFullUrl(endpoint));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
                conn.setReadTimeout(Constants.READ_TIMEOUT);

                // 헤더 설정
                conn.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty(Constants.HEADER_AUTHORIZATION, Constants.getBearerToken(token));
                }

                // 응답 처리
                handleResponse(conn, callback);

            } catch (Exception e) {
                Log.e(TAG, "GET request failed: " + e.getMessage());
                runOnMainThread(() -> callback.onError("네트워크 오류: " + e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * POST 요청
     * @param endpoint API 엔드포인트
     * @param token JWT 토큰 (nullable)
     * @param jsonBody JSON 요청 바디
     * @param callback 응답 콜백
     */
    public static void post(String endpoint, String token, String jsonBody, ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(Constants.getFullUrl(endpoint));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
                conn.setReadTimeout(Constants.READ_TIMEOUT);
                conn.setDoOutput(true);

                // 헤더 설정
                conn.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty(Constants.HEADER_AUTHORIZATION, Constants.getBearerToken(token));
                }

                // 요청 바디 전송
                if (jsonBody != null && !jsonBody.isEmpty()) {
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();
                }

                // 응답 처리
                handleResponse(conn, callback);

            } catch (Exception e) {
                Log.e(TAG, "POST request failed: " + e.getMessage());
                runOnMainThread(() -> callback.onError("네트워크 오류: " + e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * DELETE 요청
     * @param endpoint API 엔드포인트
     * @param token JWT 토큰
     * @param callback 응답 콜백
     */
    public static void delete(String endpoint, String token, ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(Constants.getFullUrl(endpoint));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
                conn.setReadTimeout(Constants.READ_TIMEOUT);

                // 헤더 설정
                conn.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty(Constants.HEADER_AUTHORIZATION, Constants.getBearerToken(token));
                }

                // 응답 처리
                handleResponse(conn, callback);

            } catch (Exception e) {
                Log.e(TAG, "DELETE request failed: " + e.getMessage());
                runOnMainThread(() -> callback.onError("네트워크 오류: " + e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * 응답 처리
     */
    private static void handleResponse(HttpURLConnection conn, ApiCallback callback) throws IOException {
        int responseCode = conn.getResponseCode();
        Log.d(TAG, "Response code: " + responseCode);

        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            // 성공
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            // 실패
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String responseBody = response.toString();
        Log.d(TAG, "Response: " + responseBody);

        // UI 스레드에서 콜백 실행
        if (responseCode >= 200 && responseCode < 300) {
            runOnMainThread(() -> callback.onSuccess(responseBody));
        } else {
            runOnMainThread(() -> callback.onError("서버 오류 (" + responseCode + "): " + responseBody));
        }
    }

    /**
     * UI 스레드에서 실행
     */
    private static void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * API 응답 콜백 인터페이스
     */
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
