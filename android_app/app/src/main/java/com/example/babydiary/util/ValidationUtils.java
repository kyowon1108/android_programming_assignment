package com.example.babydiary.util;

import android.util.Patterns;

/**
 * 입력 검증 유틸리티
 */
public class ValidationUtils {

    /**
     * 이메일 유효성 검사
     * @param email 이메일
     * @return 유효하면 true
     */
    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * 비밀번호 유효성 검사
     * @param password 비밀번호
     * @return 유효하면 true (최소 6자)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * 닉네임 유효성 검사
     * @param nickname 닉네임
     * @return 유효하면 true (2-100자)
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && nickname.length() >= 2 && nickname.length() <= 100;
    }

    /**
     * 다이어리 내용 유효성 검사
     * @param description 다이어리 내용
     * @return 유효하면 true (1-5000자)
     */
    public static boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty() && description.length() <= 5000;
    }

    /**
     * 문자열이 비어있는지 확인
     * @param str 문자열
     * @return 비어있으면 true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
