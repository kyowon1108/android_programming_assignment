package com.example.babydiary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 확인 다이얼로그
 * 사용자에게 예/아니오 선택을 요청하는 다이얼로그
 */
public class ConfirmDialog {

    /**
     * 확인 다이얼로그 리스너
     */
    public interface OnConfirmListener {
        void onConfirm();
        void onCancel();
    }

    private Context context;
    private String title;
    private String message;
    private String positiveButtonText = "확인";
    private String negativeButtonText = "취소";
    private OnConfirmListener listener;

    /**
     * 생성자
     * @param context Context
     */
    public ConfirmDialog(Context context) {
        this.context = context;
    }

    /**
     * 제목 설정
     * @param title 제목
     * @return ConfirmDialog
     */
    public ConfirmDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 메시지 설정
     * @param message 메시지
     * @return ConfirmDialog
     */
    public ConfirmDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 긍정 버튼 텍스트 설정
     * @param text 버튼 텍스트
     * @return ConfirmDialog
     */
    public ConfirmDialog setPositiveButtonText(String text) {
        this.positiveButtonText = text;
        return this;
    }

    /**
     * 부정 버튼 텍스트 설정
     * @param text 버튼 텍스트
     * @return ConfirmDialog
     */
    public ConfirmDialog setNegativeButtonText(String text) {
        this.negativeButtonText = text;
        return this;
    }

    /**
     * 리스너 설정
     * @param listener OnConfirmListener
     * @return ConfirmDialog
     */
    public ConfirmDialog setListener(OnConfirmListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 다이얼로그 표시
     */
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) {
            builder.setTitle(title);
        }

        if (message != null) {
            builder.setMessage(message);
        }

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });

        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });

        builder.setCancelable(true);
        builder.show();
    }

    /**
     * 간단한 확인 다이얼로그 표시
     * @param context Context
     * @param title 제목
     * @param message 메시지
     * @param listener 리스너
     */
    public static void showSimple(Context context, String title, String message, OnConfirmListener listener) {
        new ConfirmDialog(context)
                .setTitle(title)
                .setMessage(message)
                .setListener(listener)
                .show();
    }
}