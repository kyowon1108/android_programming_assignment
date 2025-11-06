package com.example.babydiary.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babydiary.R;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.model.Tag;
import com.example.babydiary.service.DiaryService;
import com.example.babydiary.util.DateUtils;
import com.bumptech.glide.Glide;

/**
 * 다이어리 상세 화면
 */
public class DiaryDetailActivity extends AppCompatActivity {
    private static final String TAG = "DiaryDetailActivity";

    private ImageView ivPhoto;
    private TextView tvDate;
    private TextView tvDescription;
    private TextView tvVisionDescription;
    private TextView tvGeneratedStory;
    private TextView tvExpertComment;
    private TextView tvEmotion;
    private TextView tvTags;
    private View progressBar;
    private View contentLayout;

    private DiaryService diaryService;
    private int diaryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        diaryService = new DiaryService();

        diaryId = getIntent().getIntExtra("diary_id", -1);
        if (diaryId == -1) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadDiary();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        ivPhoto = findViewById(R.id.iv_photo);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.tv_description);
        tvVisionDescription = findViewById(R.id.tv_vision_description);
        tvGeneratedStory = findViewById(R.id.tv_generated_story);
        tvExpertComment = findViewById(R.id.tv_expert_comment);
        tvEmotion = findViewById(R.id.tv_emotion);
        tvTags = findViewById(R.id.tv_tags);
        progressBar = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);
    }

    /**
     * 다이어리 로드
     */
    private void loadDiary() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        diaryService.getDiary(this, diaryId, new OnApiResponseListener<Diary>() {
            @Override
            public void onSuccess(Diary diary) {
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);

                displayDiary(diary);
                Log.d(TAG, "Diary loaded: " + diary.getDiaryId());
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load diary error: " + error);
                Toast.makeText(DiaryDetailActivity.this, "로드 실패: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 다이어리 정보 표시
     * @param diary 다이어리
     */
    private void displayDiary(Diary diary) {
        // 날짜
        tvDate.setText(DateUtils.formatDateKorean(diary.getDate()));

        // 설명
        tvDescription.setText(diary.getDescription());

        // 사진
        String photoUrl = diary.getFullPhotoUrl();
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .centerCrop()
                    .into(ivPhoto);
        }

        // Vision 설명
        if (diary.getVisionDescription() != null && !diary.getVisionDescription().isEmpty()) {
            tvVisionDescription.setText(diary.getVisionDescription());
            tvVisionDescription.setVisibility(View.VISIBLE);
        } else {
            tvVisionDescription.setVisibility(View.GONE);
        }

        // 생성된 이야기
        if (diary.getGeneratedStory() != null && !diary.getGeneratedStory().isEmpty()) {
            tvGeneratedStory.setText(diary.getGeneratedStory());
            tvGeneratedStory.setVisibility(View.VISIBLE);
        } else {
            tvGeneratedStory.setVisibility(View.GONE);
        }

        // 전문가 코멘트
        if (diary.getExpertComment() != null && !diary.getExpertComment().isEmpty()) {
            tvExpertComment.setText(diary.getExpertComment());
            tvExpertComment.setVisibility(View.VISIBLE);
        } else {
            tvExpertComment.setVisibility(View.GONE);
        }

        // 감정
        if (diary.getEmotion() != null && !diary.getEmotion().isEmpty()) {
            tvEmotion.setText("감정: " + diary.getEmotion());
            tvEmotion.setVisibility(View.VISIBLE);
        } else {
            tvEmotion.setVisibility(View.GONE);
        }

        // 태그
        if (diary.getTags() != null && !diary.getTags().isEmpty()) {
            StringBuilder tagsText = new StringBuilder("태그: ");
            for (int i = 0; i < diary.getTags().size(); i++) {
                Tag tag = diary.getTags().get(i);
                tagsText.append(tag.getName());
                if (i < diary.getTags().size() - 1) {
                    tagsText.append(", ");
                }
            }
            tvTags.setText(tagsText.toString());
            tvTags.setVisibility(View.VISIBLE);
        } else {
            tvTags.setVisibility(View.GONE);
        }
    }
}
