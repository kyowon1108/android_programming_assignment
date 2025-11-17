package com.example.babydiary.ui.diary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.data.api.DiaryApi;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.dto.MessageResponse;
import com.example.babydiary.data.dto.Tag;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.ActivityDiaryDetailBinding;
import com.example.babydiary.utils.AuthUtils;
import com.example.babydiary.utils.DateUtils;
import com.google.android.material.chip.Chip;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryDetailActivity extends AppCompatActivity {

    private ActivityDiaryDetailBinding binding;
    private DiaryApi diaryApi;
    private int diaryId;
    private DiaryResponse currentDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // API 초기화
        diaryApi = ApiClient.getClient().create(DiaryApi.class);

        // Intent에서 diary_id 가져오기
        diaryId = getIntent().getIntExtra("diary_id", -1);
        if (diaryId == -1) {
            Toast.makeText(this, "다이어리 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews();
        loadDiaryDetail();
    }

    private void setupViews() {
        // 뒤로가기 버튼
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 삭제 버튼
        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
    }

    private void loadDiaryDetail() {
        showLoading(true);

        diaryApi.getDiaryById(diaryId).enqueue(new Callback<DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryResponse> call, Response<DiaryResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentDiary = response.body();
                    displayDiary();
                } else if (response.code() == 404) {
                    Toast.makeText(DiaryDetailActivity.this,
                        "다이어리를 찾을 수 없습니다", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(DiaryDetailActivity.this);
                } else {
                    Toast.makeText(DiaryDetailActivity.this,
                        "데이터 로딩 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(DiaryDetailActivity.this,
                    "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayDiary() {
        if (currentDiary == null) return;

        // 사진 표시
        if (!TextUtils.isEmpty(currentDiary.getPhotoUrl())) {
            Glide.with(this)
                    .load(currentDiary.getPhotoUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(binding.ivPhoto);
        }

        // 날짜 표시
        binding.tvDate.setText(DateUtils.serverDateToDisplay(currentDiary.getDate()));

        // 감정 표시
        if (!TextUtils.isEmpty(currentDiary.getEmotion())) {
            binding.tvEmotion.setText(getEmotionEmoji(currentDiary.getEmotion()));
            binding.tvEmotion.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmotion.setVisibility(View.GONE);
        }

        // 설명 표시
        binding.tvDescription.setText(currentDiary.getDescription());

        // 태그 표시
        displayTags();

        // AI 이미지 분석 표시
        if (!TextUtils.isEmpty(currentDiary.getVisionDescription())) {
            binding.tvVisionDescription.setText(currentDiary.getVisionDescription());
            binding.cardVision.setVisibility(View.VISIBLE);
        } else {
            binding.cardVision.setVisibility(View.GONE);
        }

        // 전문가 코멘트 표시
        if (!TextUtils.isEmpty(currentDiary.getExpertComment())) {
            binding.tvExpertComment.setText(currentDiary.getExpertComment());
            binding.cardExpertComment.setVisibility(View.VISIBLE);
        } else {
            binding.cardExpertComment.setVisibility(View.GONE);
        }
    }

    private void displayTags() {
        binding.chipGroupTags.removeAllViews();

        if (currentDiary.getTags() != null && !currentDiary.getTags().isEmpty()) {
            for (Tag tag : currentDiary.getTags()) {
                Chip chip = new Chip(this);
                chip.setText(tag.getTagName());
                chip.setClickable(false);
                chip.setCheckable(false);
                binding.chipGroupTags.addView(chip);
            }
            binding.chipGroupTags.setVisibility(View.VISIBLE);
        } else {
            binding.chipGroupTags.setVisibility(View.GONE);
        }
    }

    private String getEmotionEmoji(String emotion) {
        switch (emotion.toLowerCase()) {
            case "joy":
            case "기쁨":
                return "😊";
            case "sadness":
            case "슬픔":
                return "😢";
            case "anger":
            case "화남":
                return "😠";
            case "surprise":
            case "놀람":
                return "😲";
            case "fear":
            case "두려움":
                return "😨";
            case "neutral":
            case "중립":
            default:
                return "😐";
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("다이어리 삭제")
                .setMessage("정말로 이 다이어리를 삭제하시겠습니까?\n삭제된 내용은 복구할 수 없습니다.")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDiary();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteDiary() {
        showLoading(true);

        diaryApi.deleteDiary(diaryId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(DiaryDetailActivity.this,
                        "다이어리가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // 삭제 성공을 알림
                    finish();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(DiaryDetailActivity.this);
                } else {
                    Toast.makeText(DiaryDetailActivity.this,
                        "삭제 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(DiaryDetailActivity.this,
                    "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnDelete.setEnabled(!show);
    }
}
