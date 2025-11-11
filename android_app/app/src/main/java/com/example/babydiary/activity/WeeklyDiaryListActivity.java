package com.example.babydiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babydiary.R;
import com.example.babydiary.adapter.WeeklyDiaryAdapter;
import com.example.babydiary.dialog.LoadingDialog;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.WeeklyDiary;
import com.example.babydiary.service.WeeklyDiaryService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 주간 다이어리 목록 화면
 */
public class WeeklyDiaryListActivity extends AppCompatActivity {
    private static final String TAG = "WeeklyDiaryListActivity";

    private RecyclerView recyclerView;
    private WeeklyDiaryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmpty;
    private View progressBar;
    private FloatingActionButton fabCreateWeekly;

    private WeeklyDiaryService weeklyDiaryService;
    private List<WeeklyDiary> weeklyDiaries;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_diary_list);

        weeklyDiaryService = new WeeklyDiaryService();
        weeklyDiaries = new ArrayList<>();
        loadingDialog = new LoadingDialog(this);

        initViews();
        setupRecyclerView();
        setupListeners();

        loadWeeklyDiaries();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.progress_bar);
        fabCreateWeekly = findViewById(R.id.fab_create_weekly);
    }

    /**
     * RecyclerView 설정
     */
    private void setupRecyclerView() {
        adapter = new WeeklyDiaryAdapter(this, weeklyDiaries, weeklyDiary -> {
            Intent intent = new Intent(this, WeeklyDiaryDetailActivity.class);
            intent.putExtra("weekly_diary_id", weeklyDiary.getWeekId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadWeeklyDiaries);
        fabCreateWeekly.setOnClickListener(v -> createWeeklyDiaryForCurrentWeek());
    }

    /**
     * 주간 다이어리 목록 로드
     */
    private void loadWeeklyDiaries() {
        progressBar.setVisibility(View.VISIBLE);

        weeklyDiaryService.getWeeklyDiaries(this, null, new OnApiResponseListener<List<WeeklyDiary>>() {
            @Override
            public void onSuccess(List<WeeklyDiary> newWeeklyDiaries) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                weeklyDiaries.clear();

                if (newWeeklyDiaries == null || newWeeklyDiaries.isEmpty()) {
                    showEmptyView(true);
                } else {
                    weeklyDiaries.addAll(newWeeklyDiaries);
                    showEmptyView(false);
                }

                adapter.notifyDataSetChanged();
                Log.d(TAG, "Weekly diaries loaded: " + weeklyDiaries.size());
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                Log.e(TAG, "Load weekly diaries error: " + error);
                Toast.makeText(WeeklyDiaryListActivity.this, "목록 로드 실패: " + error, Toast.LENGTH_SHORT).show();

                if (weeklyDiaries.isEmpty()) {
                    showEmptyView(true);
                }
            }
        });
    }

    /**
     * 빈 화면 표시
     * @param show 표시 여부
     */
    private void showEmptyView(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * 현재 주의 주간 다이어리 생성
     */
    private void createWeeklyDiaryForCurrentWeek() {
        // 현재 연도 및 주차 계산
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR);

        Log.d(TAG, "Creating weekly diary for Year: " + currentYear + ", Week: " + currentWeekNumber);

        loadingDialog.show();

        // 1단계: 이미 생성된 주간 다이어리가 있는지 확인
        weeklyDiaryService.getWeeklyDiaryByDate(this, currentYear, currentWeekNumber, new OnApiResponseListener<WeeklyDiary>() {
            @Override
            public void onSuccess(WeeklyDiary existingDiary) {
                // 이미 주간 다이어리가 존재하는 경우
                loadingDialog.dismiss();
                Log.d(TAG, "Weekly diary already exists: " + existingDiary.getWeekId());

                Toast.makeText(WeeklyDiaryListActivity.this,
                        currentYear + "년 " + currentWeekNumber + "주차 다이어리가 이미 존재합니다.\n다시 생성하시겠습니까?",
                        Toast.LENGTH_LONG).show();

                // 재생성 여부 확인 (사용자 선택 없이 바로 재생성)
                recreateWeeklyDiary(currentYear, currentWeekNumber);
            }

            @Override
            public void onError(String error) {
                // 주간 다이어리가 없는 경우 (404 에러) - 정상적으로 생성 진행
                if (error.contains("404") || error.contains("not found")) {
                    Log.d(TAG, "Weekly diary not found, creating new one");
                    createWeeklyDiaryApi(currentYear, currentWeekNumber);
                } else {
                    // 네트워크 오류 등 다른 에러
                    loadingDialog.dismiss();
                    Log.e(TAG, "Check weekly diary error: " + error);
                    Toast.makeText(WeeklyDiaryListActivity.this,
                            "주간 다이어리 확인 실패: " + error,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 주간 다이어리 재생성 (기존 것 업데이트)
     * @param year 연도
     * @param weekNumber 주차
     */
    private void recreateWeeklyDiary(int year, int weekNumber) {
        loadingDialog.show();
        createWeeklyDiaryApi(year, weekNumber);
    }

    /**
     * 주간 다이어리 생성 API 호출
     * @param year 연도
     * @param weekNumber 주차
     */
    private void createWeeklyDiaryApi(int year, int weekNumber) {
        weeklyDiaryService.createWeeklyDiary(this, year, weekNumber, new OnApiResponseListener<WeeklyDiary>() {
            @Override
            public void onSuccess(WeeklyDiary weeklyDiary) {
                loadingDialog.dismiss();
                Log.d(TAG, "Weekly diary created successfully: " + weeklyDiary.getWeekId());
                Toast.makeText(WeeklyDiaryListActivity.this,
                        year + "년 " + weekNumber + "주차 다이어리가 생성되었습니다!",
                        Toast.LENGTH_SHORT).show();

                // 목록 새로고침
                loadWeeklyDiaries();
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                Log.e(TAG, "Create weekly diary error: " + error);

                // 에러 메시지 분석 및 사용자 친화적 메시지 표시
                String userMessage;
                if (error.contains("No diaries found for this week")) {
                    userMessage = "이번 주(" + year + "년 " + weekNumber + "주차)에 작성한 일일 다이어리가 없습니다.\n" +
                            "주간 다이어리를 생성하려면 최소 1개 이상의 일일 다이어리가 필요합니다.";
                } else if (error.contains("400")) {
                    userMessage = "이번 주에 작성한 일일 다이어리가 없습니다.\n" +
                            "먼저 일일 다이어리를 작성해주세요.";
                } else {
                    userMessage = "주간 다이어리 생성 실패: " + error;
                }

                Toast.makeText(WeeklyDiaryListActivity.this, userMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeeklyDiaries();
    }
}
