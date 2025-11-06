package com.example.babydiary.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babydiary.R;
import com.example.babydiary.adapter.DiaryAdapter;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.listener.OnDiaryClickListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.model.WeeklyDiary;
import com.example.babydiary.service.WeeklyDiaryService;

/**
 * 주간 다이어리 상세 화면
 */
public class WeeklyDiaryDetailActivity extends AppCompatActivity implements OnDiaryClickListener {
    private static final String TAG = "WeeklyDiaryDetailActivity";

    private TextView tvWeekTitle;
    private TextView tvDateRange;
    private RecyclerView recyclerView;
    private DiaryAdapter adapter;
    private View progressBar;
    private View contentLayout;

    private WeeklyDiaryService weeklyDiaryService;
    private int weeklyDiaryId;

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
        loadWeeklyDiary();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        tvWeekTitle = findViewById(R.id.tv_week_title);
        tvDateRange = findViewById(R.id.tv_date_range);
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
}
