package com.example.babydiary.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 권한 관리 유틸리티
 */
public class PermissionUtils {

    /**
     * 카메라 권한 확인
     */
    public static boolean checkCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 카메라 권한 요청
     */
    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                Constants.REQUEST_PERMISSION_CAMERA
        );
    }

    /**
     * 저장소 읽기 권한 확인
     */
    public static boolean checkStorageReadPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+): READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 이하: READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 저장소 읽기 권한 요청
     */
    public static void requestStorageReadPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    Constants.REQUEST_PERMISSION_STORAGE
            );
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_PERMISSION_STORAGE
            );
        }
    }

    /**
     * 저장소 쓰기 권한 확인 (Android 10 미만)
     */
    public static boolean checkStorageWritePermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        // Android 10+ (API 29+)에서는 Scoped Storage 사용, 권한 불필요
        return true;
    }

    /**
     * 저장소 쓰기 권한 요청 (Android 10 미만)
     */
    public static void requestStorageWritePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.REQUEST_PERMISSION_STORAGE
            );
        }
    }

    /**
     * 모든 필요한 권한이 승인되었는지 확인
     */
    public static boolean checkAllPermissions(Context context) {
        return checkCameraPermission(context) && checkStorageReadPermission(context);
    }

    /**
     * 모든 필요한 권한 요청
     */
    public static void requestAllPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES
                    },
                    Constants.REQUEST_PERMISSION_CAMERA
            );
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    Constants.REQUEST_PERMISSION_CAMERA
            );
        }
    }

    /**
     * 권한 요청 결과 확인
     */
    public static boolean isPermissionGranted(int[] grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
