package com.example.babydiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.babydiary.R;

/**
 * 로딩 다이얼로그
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
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
}
