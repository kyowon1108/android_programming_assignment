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
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.WeeklyDiary;
import com.example.babydiary.service.WeeklyDiaryService;

import java.util.ArrayList;
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

    private WeeklyDiaryService weeklyDiaryService;
    private List<WeeklyDiary> weeklyDiaries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_diary_list);

        weeklyDiaryService = new WeeklyDiaryService();
        weeklyDiaries = new ArrayList<>();

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

    @Override
    protected void onResume() {
        super.onResume();
        loadWeeklyDiaries();
    }
}
