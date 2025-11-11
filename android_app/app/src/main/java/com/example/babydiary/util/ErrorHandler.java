package com.example.babydiary.util;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.example.babydiary.activity.LoginActivity;

/**
 * 공통 에러 처리 유틸리티
 */
public class ErrorHandler {

    /**
     * HTTP 상태 코드에 따른 에러 처리
     * @param statusCode HTTP 상태 코드
     * @param errorMessage 에러 메시지
     * @param context Context
     */
    public static void handleError(int statusCode, String errorMessage, Context context) {
        String title = "오류";
        String message = "";
        boolean shouldLogout = false;

        switch (statusCode) {
            case 400:
                message = "잘못된 요청입니다.\n" + errorMessage;
                break;
            case 401:
                title = "세션 만료";
                message = "로그인 정보가 만료되었습니다.\n다시 로그인해주세요.";
                shouldLogout = true;
                break;
            case 403:
                title = "접근 거부";
                message = "해당 작업을 수행할 권한이 없습니다.";
                break;
            case 404:
                message = "요청한 데이터를 찾을 수 없습니다.";
                break;
            case 500:
                message = "서버 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.";
                break;
            case -1: // Network/Timeout
                title = "네트워크 오류";
                message = "인터넷 연결을 확인해주세요.";
                break;
            default:
                message = "예상치 못한 오류가 발생했습니다.\n" + errorMessage;
        }

        final boolean logout = shouldLogout;

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", (dialog, which) -> {
                    if (logout) {
                        // 토큰 삭제
                        SharedPrefsManager.clearToken(context);

                        // LoginActivity로 이동
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 에러 메시지에서 HTTP 상태 코드 추출
     * @param errorMessage 에러 메시지
     * @return HTTP 상태 코드 (추출 실패 시 -1)
     */
    public static int extractStatusCode(String errorMessage) {
        if (errorMessage == null) {
            return -1;
        }

        // "HTTP 400", "400 Bad Request" 등의 패턴에서 상태 코드 추출
        if (errorMessage.contains("401") || errorMessage.contains("Unauthorized")) {
            return 401;
        } else if (errorMessage.contains("400") || errorMessage.contains("Bad Request")) {
            return 400;
        } else if (errorMessage.contains("403") || errorMessage.contains("Forbidden")) {
            return 403;
        } else if (errorMessage.contains("404") || errorMessage.contains("Not Found")) {
            return 404;
        } else if (errorMessage.contains("500") || errorMessage.contains("Internal Server Error")) {
            return 500;
        } else if (errorMessage.contains("timeout") || errorMessage.contains("network")) {
            return -1;
        }

        return -1;
    }

    /**
     * 에러 메시지를 사용자 친화적으로 변환
     * @param error 에러 메시지
     * @param context Context
     */
    public static void handleApiError(String error, Context context) {
        int statusCode = extractStatusCode(error);
        handleError(statusCode, error, context);
    }
}
