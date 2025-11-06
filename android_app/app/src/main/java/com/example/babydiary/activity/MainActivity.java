package com.example.babydiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babydiary.R;
import com.example.babydiary.adapter.DiaryAdapter;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.listener.OnDiaryClickListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.service.DiaryService;
import com.example.babydiary.util.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 메인 화면 (다이어리 목록)
 */
public class MainActivity extends AppCompatActivity implements OnDiaryClickListener {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private DiaryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmpty;
    private FloatingActionButton fabCreate;
    private View progressBar;

    private DiaryService diaryService;
    private List<Diary> diaries;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        diaryService = new DiaryService();
        diaries = new ArrayList<>();

        initViews();
        setupRecyclerView();
        setupListeners();

        loadDiaries(false);
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tvEmpty = findViewById(R.id.tv_empty);
        fabCreate = findViewById(R.id.fab_create);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * RecyclerView 설정
     */
    private void setupRecyclerView() {
        adapter = new DiaryAdapter(this, diaries, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 무한 스크롤
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMorePages) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (totalItemCount <= (lastVisibleItem + 2)) {
                        loadDiaries(false);
                    }
                }
            }
        });
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        fabCreate.setOnClickListener(v -> navigateToCreateDiary());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            hasMorePages = true;
            loadDiaries(true);
        });
    }

    /**
     * 다이어리 목록 로드
     * @param isRefresh 새로고침 여부
     */
    private void loadDiaries(boolean isRefresh) {
        if (isLoading) return;

        isLoading = true;

        if (!isRefresh && currentPage == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        diaryService.getDiaries(this, currentPage, PAGE_SIZE, new OnApiResponseListener<List<Diary>>() {
            @Override
            public void onSuccess(List<Diary> newDiaries) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (isRefresh) {
                    diaries.clear();
                }

                if (newDiaries == null || newDiaries.isEmpty()) {
                    hasMorePages = false;
                    if (diaries.isEmpty()) {
                        showEmptyView(true);
                    }
                } else {
                    diaries.addAll(newDiaries);
                    currentPage++;
                    showEmptyView(false);

                    if (newDiaries.size() < PAGE_SIZE) {
                        hasMorePages = false;
                    }
                }

                adapter.notifyDataSetChanged();
                Log.d(TAG, "Diaries loaded: " + diaries.size());
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                Log.e(TAG, "Load diaries error: " + error);
                Toast.makeText(MainActivity.this, "목록 로드 실패: " + error, Toast.LENGTH_SHORT).show();

                if (diaries.isEmpty()) {
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
     * 다이어리 작성 화면으로 이동
     */
    private void navigateToCreateDiary() {
        Intent intent = new Intent(this, CreateDiaryActivity.class);
        startActivity(intent);
    }

    /**
     * 다이어리 상세 화면으로 이동
     * @param diary 다이어리
     */
    @Override
    public void onDiaryClick(Diary diary) {
        Intent intent = new Intent(this, DiaryDetailActivity.class);
        intent.putExtra("diary_id", diary.getDiaryId());
        startActivity(intent);
    }

    /**
     * 다이어리 삭제
     * @param diary 다이어리
     */
    @Override
    public void onDiaryDelete(Diary diary) {
        diaryService.deleteDiary(this, diary.getDiaryId(), new OnApiResponseListener<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                Toast.makeText(MainActivity.this, "다이어리가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                diaries.remove(diary);
                adapter.notifyDataSetChanged();

                if (diaries.isEmpty()) {
                    showEmptyView(true);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "삭제 실패: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 작성/수정 후 돌아왔을 때 목록 새로고침
        currentPage = 1;
        hasMorePages = true;
        loadDiaries(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            navigateToProfile();
            return true;
        } else if (id == R.id.action_weekly) {
            navigateToWeekly();
            return true;
        } else if (id == R.id.action_logout) {
            handleLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 프로필 화면으로 이동
     */
    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * 주간 다이어리 화면으로 이동
     */
    private void navigateToWeekly() {
        Intent intent = new Intent(this, WeeklyDiaryListActivity.class);
        startActivity(intent);
    }

    /**
     * 로그아웃 처리
     */
    private void handleLogout() {
        SharedPrefsManager.clearToken(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
