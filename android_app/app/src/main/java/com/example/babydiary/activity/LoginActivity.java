package com.example.babydiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babydiary.R;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.User;
import com.example.babydiary.service.AuthService;
import com.example.babydiary.util.SharedPrefsManager;
import com.example.babydiary.util.ValidationUtils;

/**
 * 로그인 화면
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private View progressBar;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이미 로그인된 경우 MainActivity로 이동
        if (SharedPrefsManager.isLoggedIn(this)) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        authService = new AuthService();

        initViews();
        setupListeners();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        tvRegister.setOnClickListener(v -> navigateToRegister());
    }

    /**
     * 로그인 처리
     */
    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 유효성 검사
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("올바른 이메일 형식이 아닙니다");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("비밀번호는 8자 이상이어야 합니다");
            etPassword.requestFocus();
            return;
        }

        showLoading(true);

        authService.login(this, email, password, new OnApiResponseListener<User>() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                Log.d(TAG, "Login success: " + user.getEmail());
                Toast.makeText(LoginActivity.this, "환영합니다, " + user.getNickname() + "님!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Log.e(TAG, "Login error: " + error);
                Toast.makeText(LoginActivity.this, "로그인 실패: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 회원가입 화면으로 이동
     */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * 메인 화면으로 이동
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 로딩 상태 표시
     * @param show 표시 여부
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
    }
}
