package com.example.babydiary.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://kaprpc.iptime.org:5051/api/v1/";
    private static final String PREF_NAME = "BabyDiaryPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static Retrofit retrofit = null;
    private static SharedPreferences sharedPreferences = null;
    private static Context appContext = null;

    // 초기화 메서드 (Application에서 호출)
    public static void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
            sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    // Retrofit 인스턴스 반환
    public static Retrofit getClient() {
        if (retrofit == null) {
            // appContext가 null인 경우 안전 처리
            if (appContext == null) {
                throw new IllegalStateException("ApiClient.init() must be called first in Application.onCreate()");
            }
            retrofit = createRetrofit();
        }
        return retrofit;
    }

    private static Retrofit createRetrofit() {
        // HTTP 로깅 인터셉터
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor())  // 인증 헤더 자동 추가
                .build();

        // Retrofit 빌더
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // 인증 토큰 저장
    public static void saveToken(String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit()
                    .putString(KEY_ACCESS_TOKEN, token)
                    .apply();
        }
    }

    // 인증 토큰 가져오기
    public static String getToken() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        }
        return null;
    }

    // 인증 토큰 삭제
    public static void clearToken() {
        if (sharedPreferences != null) {
            sharedPreferences.edit()
                    .remove(KEY_ACCESS_TOKEN)
                    .apply();
        }
    }

    // 토큰 존재 여부 확인
    public static boolean hasToken() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    // Authorization 헤더 자동 추가 인터셉터
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            String token = getToken();
            if (token == null || token.isEmpty()) {
                return chain.proceed(originalRequest);
            }

            // Authorization 헤더 추가
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            return chain.proceed(newRequest);
        }
    }
}