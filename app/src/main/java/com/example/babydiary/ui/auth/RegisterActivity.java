package com.example.babydiary.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babydiary.data.api.AuthApi;
import com.example.babydiary.data.dto.MessageResponse;
import com.example.babydiary.data.dto.RegisterRequest;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.ActivityRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // API 초기화
        authApi = ApiClient.getClient().create(AuthApi.class);

        setupViews();
    }

    private void setupViews() {
        // 뒤로가기 버튼
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 회원가입 버튼 클릭
        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        // 로그인 링크 클릭
        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        // 입력값 가져오기
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String passwordConfirm = binding.etPasswordConfirm.getText().toString().trim();
        String nickname = binding.etNickname.getText().toString().trim();

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

        if (password.length() < 6) {
            binding.etPassword.setError("비밀번호는 6자 이상이어야 합니다");
            binding.etPassword.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            binding.etPasswordConfirm.setError("비밀번호가 일치하지 않습니다");
            binding.etPasswordConfirm.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            binding.etNickname.setError("닉네임을 입력해주세요");
            binding.etNickname.requestFocus();
            return;
        }

        // 로딩 표시
        showLoading(true);

        // 회원가입 API 호출
        RegisterRequest request = new RegisterRequest(email, password, nickname);
        authApi.register(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "회원가입 성공! 로그인해주세요.", Toast.LENGTH_LONG).show();
                    finish(); // LoginActivity로 돌아가기
                } else if (response.code() == 400) {
                    Toast.makeText(RegisterActivity.this, "이미 사용중인 이메일입니다", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "회원가입 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(RegisterActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!show);
        binding.etEmail.setEnabled(!show);
        binding.etPassword.setEnabled(!show);
        binding.etPasswordConfirm.setEnabled(!show);
        binding.etNickname.setEnabled(!show);
    }
}