package com.example.babydiary;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Baby Diary Application 클래스
 * 앱 전역 설정 및 초기화를 담당
 */
public class BabyDiaryApplication extends Application {
    private static final String TAG = "BabyDiaryApp";
    private static BabyDiaryApplication instance;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();

        Log.d(TAG, "BabyDiary Application started");

        // 전역 초기화 작업
        initializeApp();
    }

    /**
     * 앱 초기화
     */
    private void initializeApp() {
        // 크래시 핸들러 설정
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, "Uncaught exception: " + throwable.getMessage(), throwable);
                // 필요시 크래시 리포트 전송
                System.exit(1);
            }
        });

        // 필요한 디렉토리 생성
        createRequiredDirectories();

        // 캐시 정리 (필요시)
        cleanupCache();
    }

    /**
     * 필요한 디렉토리 생성
     */
    private void createRequiredDirectories() {
        try {
            // 이미지 캐시 디렉토리
            java.io.File imageCache = new java.io.File(getCacheDir(), "images");
            if (!imageCache.exists()) {
                imageCache.mkdirs();
                Log.d(TAG, "Image cache directory created");
            }

            // 임시 파일 디렉토리
            java.io.File tempDir = new java.io.File(getFilesDir(), "temp");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
                Log.d(TAG, "Temp directory created");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to create directories", e);
        }
    }

    /**
     * 캐시 정리
     */
    private void cleanupCache() {
        try {
            // 7일 이상 된 캐시 파일 삭제
            java.io.File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                long currentTime = System.currentTimeMillis();
                long sevenDaysAgo = currentTime - (7 * 24 * 60 * 60 * 1000);

                java.io.File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (java.io.File file : files) {
                        if (file.lastModified() < sevenDaysAgo) {
                            file.delete();
                            Log.d(TAG, "Deleted old cache file: " + file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to cleanup cache", e);
        }
    }

    /**
     * Application 인스턴스 반환
     */
    public static BabyDiaryApplication getInstance() {
        return instance;
    }

    /**
     * Application Context 반환
     */
    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "Low memory warning received");
        // 메모리 부족 시 캐시 정리
        cleanupCache();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.w(TAG, "Trim memory level: " + level);

        if (level >= TRIM_MEMORY_MODERATE) {
            // 메모리 해제가 필요한 경우
            cleanupCache();
        }
    }
}