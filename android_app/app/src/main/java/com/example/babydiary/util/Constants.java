package com.example.babydiary.util;

import android.os.Build;

/**
 * 애플리케이션 전역 상수 정의
 */
public class Constants {

    // ==================== API 설정 ====================

    /**
     * 에뮬레이터에서 호스트 PC의 localhost 접근
     * Android 에뮬레이터는 10.0.2.2를 호스트 PC의 localhost로 라우팅
     */
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";

    /**
     * 실제 기기에서 접근
     * 같은 Wi-Fi 네트워크에 연결된 PC의 IP 주소로 변경 필요
     * 예: "http://192.168.0.100:8000"
     */
    private static final String BASE_URL_DEVICE = "http://192.168.0.100:8000";

    /**
     * 현재 환경에 맞는 Base URL 자동 선택
     */
    public static final String BASE_URL = isEmulator() ? BASE_URL_EMULATOR : BASE_URL_DEVICE;

    // API 엔드포인트
    public static final String API_VERSION = "/api/v1";

    // 인증
    public static final String ENDPOINT_REGISTER = API_VERSION + "/auth/register";
    public static final String ENDPOINT_LOGIN = API_VERSION + "/auth/login";
    public static final String ENDPOINT_ME = API_VERSION + "/auth/me";
    public static final String ENDPOINT_REFRESH = API_VERSION + "/auth/refresh";

    // 다이어리
    public static final String ENDPOINT_DIARIES = API_VERSION + "/diaries";
    public static final String ENDPOINT_DIARY_BY_ID = API_VERSION + "/diaries/%d"; // diary_id

    // 주간 다이어리
    public static final String ENDPOINT_WEEKLY_DIARIES = API_VERSION + "/weekly_diaries";
    public static final String ENDPOINT_WEEKLY_DIARY_BY_ID = API_VERSION + "/weekly_diaries/%d"; // week_id
    public static final String ENDPOINT_WEEKLY_DIARY_BY_DATE = API_VERSION + "/weekly_diaries/by-date/%d/%d"; // year, week_number

    // 태그
    public static final String ENDPOINT_TAGS = API_VERSION + "/tags";
    public static final String ENDPOINT_TAG_CATEGORIES = API_VERSION + "/tags/categories";
    public static final String ENDPOINT_TAG_BY_CATEGORY = API_VERSION + "/tags/category/%s"; // category


    // ==================== SharedPreferences 키 ====================

    public static final String PREF_NAME = "BabyDiaryPrefs";
    public static final String PREF_ACCESS_TOKEN = "access_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_NICKNAME = "user_nickname";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";


    // ==================== HTTP 설정 ====================

    public static final int CONNECTION_TIMEOUT = 30000; // 30초
    public static final int READ_TIMEOUT = 30000; // 30초
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";


    // ==================== 이미지 설정 ====================

    public static final int MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final int IMAGE_COMPRESSION_QUALITY = 80; // 0-100
    public static final int IMAGE_QUALITY = 80; // 0-100 (alias)
    public static final int MAX_IMAGE_WIDTH = 1920;
    public static final int MAX_IMAGE_HEIGHT = 1080;


    // ==================== 페이징 설정 ====================

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;


    // ==================== 감정 타입 ====================

    public static final String EMOTION_JOY = "joy";
    public static final String EMOTION_SADNESS = "sadness";
    public static final String EMOTION_ANGER = "anger";
    public static final String EMOTION_SURPRISE = "surprise";
    public static final String EMOTION_FEAR = "fear";
    public static final String EMOTION_NEUTRAL = "neutral";


    // ==================== 요청 코드 ====================

    public static final int REQUEST_IMAGE_CAPTURE = 1001;
    public static final int REQUEST_IMAGE_PICK = 1002;
    public static final int REQUEST_PERMISSION_CAMERA = 2001;
    public static final int REQUEST_PERMISSION_STORAGE = 2002;


    // ==================== 에러 메시지 ====================

    public static final String ERROR_NETWORK = "네트워크 연결을 확인해주세요.";
    public static final String ERROR_SERVER = "서버 오류가 발생했습니다.";
    public static final String ERROR_UNAUTHORIZED = "로그인이 필요합니다.";
    public static final String ERROR_INVALID_INPUT = "입력값을 확인해주세요.";
    public static final String ERROR_IMAGE_TOO_LARGE = "이미지 크기가 너무 큽니다.";
    public static final String ERROR_PERMISSION_DENIED = "권한이 필요합니다.";


    // ==================== 유틸리티 메서드 ====================

    /**
     * 현재 디바이스가 에뮬레이터인지 확인
     * @return 에뮬레이터면 true, 실제 기기면 false
     */
    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    /**
     * 전체 URL 생성
     * @param endpoint API 엔드포인트
     * @return 전체 URL
     */
    public static String getFullUrl(String endpoint) {
        return BASE_URL + endpoint;
    }

    /**
     * Authorization 헤더 값 생성
     * @param token JWT 토큰
     * @return "Bearer {token}"
     */
    public static String getBearerToken(String token) {
        return "Bearer " + token;
    }
}
