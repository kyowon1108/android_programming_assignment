package com.example.babydiary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.ui.auth.LoginActivity;

/**
 * Handles authentication failures consistently across screens.
 */
public final class AuthUtils {

    private AuthUtils() {
        // Utility class
    }

    public static void handleUnauthorized(Context context) {
        if (context == null) {
            return;
        }

        ApiClient.clearToken();
        Toast.makeText(context, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
