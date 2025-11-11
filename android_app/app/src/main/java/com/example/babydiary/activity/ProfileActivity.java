package com.example.babydiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babydiary.R;
import com.example.babydiary.dialog.ConfirmDialog;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.User;
import com.example.babydiary.service.AuthService;
import com.bumptech.glide.Glide;

/**
 * 프로필 화면
 */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private ImageView ivProfile;
    private TextView tvNickname;
    private TextView tvEmail;
    private TextView tvBestStreak;
    private TextView tvCurrentStreak;
    private Button btnLogout;
    private View progressBar;
    private View contentLayout;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authService = new AuthService();

        initViews();
        setupListeners();
        loadProfile();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        ivProfile = findViewById(R.id.iv_profile);
        tvNickname = findViewById(R.id.tv_nickname);
        tvEmail = findViewById(R.id.tv_email);
        tvBestStreak = findViewById(R.id.tv_best_streak);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    /**
     * 프로필 정보 로드
     */
    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        authService.getCurrentUser(this, new OnApiResponseListener<User>() {
            @Override
            public void onSuccess(User user) {
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);

                displayProfile(user);
                Log.d(TAG, "Profile loaded: " + user.getEmail());
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load profile error: " + error);
                Toast.makeText(ProfileActivity.this, "프로필 로드 실패: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 프로필 정보 표시
     * @param user 사용자 정보
     */
    private void displayProfile(User user) {
        tvNickname.setText(user.getNickname());
        tvEmail.setText(user.getEmail());
        tvBestStreak.setText("최고 연속 기록: " + user.getBestStreak() + "일");
        tvCurrentStreak.setText("현재 연속 기록: " + user.getCurrentStreak() + "일");

        // 프로필 이미지
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getFullProfileImageUrl())
                    .circleCrop()
                    .into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.ic_profile_default);
        }
    }

    /**
     * 로그아웃 확인 다이얼로그 표시
     */
    private void showLogoutConfirmDialog() {
        new ConfirmDialog(this)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButtonText("로그아웃")
                .setNegativeButtonText("취소")
                .setListener(new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        performLogout();
                    }

                    @Override
                    public void onCancel() {
                        // 아무것도 하지 않음
                    }
                })
                .show();
    }

    /**
     * 로그아웃 실행
     */
    private void performLogout() {
        // SharedPreferences에서 토큰 및 사용자 정보 삭제
        authService.logout(this);

        Log.d(TAG, "User logged out successfully");
        Toast.makeText(this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show();

        // LoginActivity로 이동하고 액티비티 스택 클리어
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
