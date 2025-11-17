package com.example.babydiary;

import android.app.Application;
import android.util.Log;
import com.example.babydiary.data.network.ApiClient;

public class BabyDiaryApplication extends Application {

    private static final String TAG = "BabyDiaryApp";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // ApiClient 초기화
            Log.d(TAG, "Initializing ApiClient...");
            ApiClient.init(this);
            Log.d(TAG, "ApiClient initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize ApiClient", e);
            e.printStackTrace();
        }
    }
}