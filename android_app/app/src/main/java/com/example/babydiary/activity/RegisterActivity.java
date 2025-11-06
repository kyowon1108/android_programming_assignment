package com.example.babydiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babydiary.R;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.User;
import com.example.babydiary.service.AuthService;
import com.example.babydiary.util.ValidationUtils;

/**
 * 회원가입 화면
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private EditText etNickname;
    private Button btnRegister;
    private View progressBar;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        etNickname = findViewById(R.id.et_nickname);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    /**
     * 회원가입 처리
     */
    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();

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

        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError("비밀번호가 일치하지 않습니다");
            etPasswordConfirm.requestFocus();
            return;
        }

        if (nickname.isEmpty() || nickname.length() < 2) {
            etNickname.setError("닉네임은 2자 이상이어야 합니다");
            etNickname.requestFocus();
            return;
        }

        showLoading(true);

        authService.register(email, password, nickname, new OnApiResponseListener<User>() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                Log.d(TAG, "Register success: " + user.getEmail());
                Toast.makeText(RegisterActivity.this, "회원가입 성공! 로그인해주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Log.e(TAG, "Register error: " + error);
                Toast.makeText(RegisterActivity.this, "회원가입 실패: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 로딩 상태 표시
     * @param show 표시 여부
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
        etPasswordConfirm.setEnabled(!show);
        etNickname.setEnabled(!show);
    }
}
