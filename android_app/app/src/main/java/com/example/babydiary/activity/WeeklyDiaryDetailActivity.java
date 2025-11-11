package com.example.babydiary.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.adapter.DiaryAdapter;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.listener.OnDiaryClickListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.model.WeeklyDiary;
import com.example.babydiary.service.WeeklyDiaryService;
import com.example.babydiary.util.Constants;

/**
 * 주간 다이어리 상세 화면
 */
public class WeeklyDiaryDetailActivity extends AppCompatActivity implements OnDiaryClickListener {
    private static final String TAG = "WeeklyDiaryDetailActivity";

    private TextView tvWeekTitle;
    private TextView tvDateRange;
    private CardView cardWeeklyImage;
    private ImageView ivWeeklyImage;
    private TextView tvWeeklyTitle;
    private TextView tvWeeklySummary;
    private RecyclerView recyclerView;
    private DiaryAdapter adapter;
    private View progressBar;
    private View contentLayout;
    private ImageButton btnBgmToggle;

    private WeeklyDiaryService weeklyDiaryService;
    private int weeklyDiaryId;

    private MediaPlayer mediaPlayer;
    private boolean isBgmPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_diary_detail);

        weeklyDiaryService = new WeeklyDiaryService();

        weeklyDiaryId = getIntent().getIntExtra("weekly_diary_id", -1);
        if (weeklyDiaryId == -1) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadWeeklyDiary();
        initBgm();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        tvWeekTitle = findViewById(R.id.tv_week_title);
        tvDateRange = findViewById(R.id.tv_date_range);
        cardWeeklyImage = findViewById(R.id.card_weekly_image);
        ivWeeklyImage = findViewById(R.id.iv_weekly_image);
        tvWeeklyTitle = findViewById(R.id.tv_weekly_title);
        tvWeeklySummary = findViewById(R.id.tv_weekly_summary);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);
    }

    /**
     * RecyclerView 설정
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 주간 다이어리 로드
     */
    private void loadWeeklyDiary() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        weeklyDiaryService.getWeeklyDiaryDetail(this, weeklyDiaryId, new OnApiResponseListener<WeeklyDiary>() {
            @Override
            public void onSuccess(WeeklyDiary weeklyDiary) {
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);

                displayWeeklyDiary(weeklyDiary);
                Log.d(TAG, "Weekly diary loaded: " + weeklyDiary.getWeekId());
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load weekly diary error: " + error);
                Toast.makeText(WeeklyDiaryDetailActivity.this, "로드 실패: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 주간 다이어리 정보 표시
     * @param weeklyDiary 주간 다이어리
     */
    private void displayWeeklyDiary(WeeklyDiary weeklyDiary) {
        tvWeekTitle.setText(weeklyDiary.getYear() + "년 " + weeklyDiary.getWeekNumber() + "주차");
        tvDateRange.setText(weeklyDiary.getStartDate() + " ~ " + weeklyDiary.getEndDate());

        // 주간 대표 이미지 표시
        if (weeklyDiary.getWeeklyImageUrl() != null && !weeklyDiary.getWeeklyImageUrl().isEmpty()) {
            cardWeeklyImage.setVisibility(View.VISIBLE);
            String imageUrl = Constants.BASE_URL + "/uploads/weekly/" + weeklyDiary.getWeeklyImageUrl();

            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(ivWeeklyImage);

            Log.d(TAG, "Weekly image loaded: " + imageUrl);
        } else {
            cardWeeklyImage.setVisibility(View.GONE);
        }

        // AI 주간 제목 표시
        if (weeklyDiary.getWeeklyTitle() != null && !weeklyDiary.getWeeklyTitle().isEmpty()) {
            tvWeeklyTitle.setVisibility(View.VISIBLE);
            tvWeeklyTitle.setText(weeklyDiary.getWeeklyTitle());
        } else {
            tvWeeklyTitle.setVisibility(View.GONE);
        }

        // AI 주간 요약 표시
        if (weeklyDiary.getWeeklySummaryText() != null && !weeklyDiary.getWeeklySummaryText().isEmpty()) {
            tvWeeklySummary.setVisibility(View.VISIBLE);
            tvWeeklySummary.setText(weeklyDiary.getWeeklySummaryText());
        } else {
            tvWeeklySummary.setVisibility(View.GONE);
        }

        // 일일 다이어리 목록 표시
        if (weeklyDiary.getDiaries() != null && !weeklyDiary.getDiaries().isEmpty()) {
            adapter = new DiaryAdapter(this, weeklyDiary.getDiaries(), this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDiaryClick(Diary diary) {
        // 다이어리 상세 화면으로 이동 (이미 MainActivity에서 처리)
        Toast.makeText(this, "다이어리 ID: " + diary.getDiaryId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDiaryDelete(Diary diary) {
        // 주간 다이어리 화면에서는 삭제 기능 비활성화
        Toast.makeText(this, "주간 다이어리에서는 삭제할 수 없습니다", Toast.LENGTH_SHORT).show();
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        // BGM 토글 버튼이 레이아웃에 있는 경우 설정
        // btnBgmToggle.setOnClickListener(v -> toggleBgm());
    }

    /**
     * BGM 초기화
     */
    private void initBgm() {
        try {
            // 샘플 BGM 사용 (res/raw 폴더에 bgm 파일 추가 필요)
            // mediaPlayer = MediaPlayer.create(this, R.raw.bgm_sample);

            // 임시로 시스템 사운드 사용
            mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_RINGTONE_URI);

            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0.5f, 0.5f);
                Log.d(TAG, "BGM initialized");
            }
        } catch (Exception e) {
            Log.e(TAG, "BGM initialization failed: " + e.getMessage());
        }
    }

    /**
     * BGM 재생/일시정지 토글
     */
    private void toggleBgm() {
        if (mediaPlayer == null) {
            initBgm();
        }

        if (mediaPlayer != null) {
            if (isBgmPlaying) {
                pauseBgm();
            } else {
                playBgm();
            }
        }
    }

    /**
     * BGM 재생
     */
    private void playBgm() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isBgmPlaying = true;
                // btnBgmToggle.setImageResource(R.drawable.ic_pause);
                Log.d(TAG, "BGM started");
            }
        } catch (Exception e) {
            Log.e(TAG, "BGM play failed: " + e.getMessage());
        }
    }

    /**
     * BGM 일시정지
     */
    private void pauseBgm() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isBgmPlaying = false;
                // btnBgmToggle.setImageResource(R.drawable.ic_play);
                Log.d(TAG, "BGM paused");
            }
        } catch (Exception e) {
            Log.e(TAG, "BGM pause failed: " + e.getMessage());
        }
    }

    /**
     * BGM 정지
     */
    private void stopBgm() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                isBgmPlaying = false;
                Log.d(TAG, "BGM stopped");
            }
        } catch (Exception e) {
            Log.e(TAG, "BGM stop failed: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseBgm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBgm();
    }
}
