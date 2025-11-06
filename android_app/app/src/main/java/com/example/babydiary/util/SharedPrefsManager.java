package com.example.babydiary.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 관리 유틸리티
 * 토큰, 사용자 정보 등 로컬 저장
 */
public class SharedPrefsManager {
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // ==================== 토큰 관리 ====================

    /**
     * JWT 토큰 저장
     */
    public static void saveToken(Context context, String token) {
        getPrefs(context).edit()
                .putString(Constants.PREF_ACCESS_TOKEN, token)
                .putBoolean(Constants.PREF_IS_LOGGED_IN, true)
                .apply();
    }

    /**
     * JWT 토큰 가져오기
     */
    public static String getToken(Context context) {
        return getPrefs(context).getString(Constants.PREF_ACCESS_TOKEN, null);
    }

    /**
     * 토큰 삭제 (로그아웃)
     */
    public static void clearToken(Context context) {
        getPrefs(context).edit()
                .remove(Constants.PREF_ACCESS_TOKEN)
                .putBoolean(Constants.PREF_IS_LOGGED_IN, false)
                .apply();
    }

    /**
     * 로그인 상태 확인
     */
    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(Constants.PREF_IS_LOGGED_IN, false)
                && getToken(context) != null;
    }

    // ==================== 사용자 정보 ====================

    /**
     * 사용자 ID 저장
     */
    public static void saveUserId(Context context, int userId) {
        getPrefs(context).edit()
                .putInt(Constants.PREF_USER_ID, userId)
                .apply();
    }

    /**
     * 사용자 ID 가져오기
     */
    public static int getUserId(Context context) {
        return getPrefs(context).getInt(Constants.PREF_USER_ID, -1);
    }

    /**
     * 이메일 저장
     */
    public static void saveEmail(Context context, String email) {
        getPrefs(context).edit()
                .putString(Constants.PREF_USER_EMAIL, email)
                .apply();
    }

    /**
     * 이메일 가져오기
     */
    public static String getEmail(Context context) {
        return getPrefs(context).getString(Constants.PREF_USER_EMAIL, null);
    }

    /**
     * 닉네임 저장
     */
    public static void saveNickname(Context context, String nickname) {
        getPrefs(context).edit()
                .putString(Constants.PREF_USER_NICKNAME, nickname)
                .apply();
    }

    /**
     * 닉네임 가져오기
     */
    public static String getNickname(Context context) {
        return getPrefs(context).getString(Constants.PREF_USER_NICKNAME, null);
    }

    // ==================== 로그아웃 ====================

    /**
     * 모든 데이터 삭제 (로그아웃)
     */
    public static void clearAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}
