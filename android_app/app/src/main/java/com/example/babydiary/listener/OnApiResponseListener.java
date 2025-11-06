package com.example.babydiary.listener;

/**
 * API 응답 콜백 인터페이스
 */
public interface OnApiResponseListener<T> {
    /**
     * API 호출 성공
     * @param data 응답 데이터
     */
    void onSuccess(T data);

    /**
     * API 호출 실패
     * @param errorMessage 에러 메시지
     */
    void onError(String errorMessage);
}
