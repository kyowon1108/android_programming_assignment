package com.example.babydiary.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babydiary.R;
import com.example.babydiary.data.api.AuthApi;
import com.example.babydiary.data.dto.LoginRequest;
import com.example.babydiary.data.dto.LoginResponse;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.ActivityLoginBinding;
import com.example.babydiary.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // ViewBinding 초기화
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // ApiClient 초기화 보장
            try {
                ApiClient.init(getApplicationContext());
            } catch (Exception ignored) {
                // 이미 초기화된 경우 무시
            }

            // API 생성
            authApi = ApiClient.getClient().create(AuthApi.class);

            // 토큰 확인
            if (ApiClient.hasToken()) {
                navigateToMain();
                return;
            }

            // 뷰 설정
            setupViews();

        } catch (Exception e) {
            e.printStackTrace();
            // 에러가 발생해도 앱이 종료되지 않도록
            Toast.makeText(this, "초기화 오류: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupViews() {
        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        // 회원가입 링크 클릭
        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // 입력값 가져오기
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // 유효성 검사
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("이메일을 입력해주세요");
            binding.etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("올바른 이메일 형식이 아닙니다");
            binding.etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("비밀번호를 입력해주세요");
            binding.etPassword.requestFocus();
            return;
        }

        // 로딩 표시
        showLoading(true);

        // 로그인 API 호출
        LoginRequest request = new LoginRequest(email, password);
        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // 토큰 저장
                    String token = response.body().getAccessToken();
                    ApiClient.saveToken(token);

                    // MainActivity로 이동
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!show);
        binding.etEmail.setEnabled(!show);
        binding.etPassword.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}