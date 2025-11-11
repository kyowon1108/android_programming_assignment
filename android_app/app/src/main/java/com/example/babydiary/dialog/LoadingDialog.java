package com.example.babydiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.babydiary.R;

/**
 * 로딩 다이얼로그
 */
public class LoadingDialog extends Dialog {
    private TextView tvLoadingMessage;
    private String message;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.message = "로딩 중...";
    }

    public LoadingDialog(@NonNull Context context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        // 배경 투명화
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 취소 불가능
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // TextView 초기화
        tvLoadingMessage = findViewById(R.id.tv_loading_message);
        if (message != null) {
            tvLoadingMessage.setText(message);
        }
    }

    /**
     * 로딩 메시지 설정
     * @param message 표시할 메시지
     */
    public void setMessage(String message) {
        this.message = message;
        if (tvLoadingMessage != null) {
            tvLoadingMessage.setText(message);
        }
    }

    /**
     * 로딩 다이얼로그 표시
     * @param context Context
     * @return LoadingDialog 인스턴스
     */
    public static LoadingDialog show(Context context) {
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.show();
        return dialog;
    }

    /**
     * 로딩 다이얼로그 표시 (메시지 포함)
     * @param context Context
     * @param message 표시할 메시지
     * @return LoadingDialog 인스턴스
     */
    public static LoadingDialog show(Context context, String message) {
        LoadingDialog dialog = new LoadingDialog(context, message);
        dialog.show();
        return dialog;
    }
}
