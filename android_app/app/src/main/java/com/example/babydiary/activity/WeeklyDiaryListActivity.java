package com.example.babydiary.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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
import com.example.babydiary.util.ImageUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
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

    private ActivityResultLauncher<Intent> galleryLauncher;
    private File selectedPhotoFile;
    private int pendingYear;
    private int pendingWeekNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_diary_list);

        weeklyDiaryService = new WeeklyDiaryService();
        weeklyDiaries = new ArrayList<>();
        loadingDialog = new LoadingDialog(this);

        initViews();
        setupActivityResultLaunchers();
        setupRecyclerView();
        setupListeners();

        loadWeeklyDiaries();
    }

    /**
     * ActivityResultLauncher 설정
     */
    private void setupActivityResultLaunchers() {
        // 갤러리
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            handleImageSelected(selectedImageUri);
                        }
                    }
                }
        );
    }

    /**
     * 이미지 선택 처리
     * @param imageUri 이미지 URI
     */
    private void handleImageSelected(Uri imageUri) {
        try {
            // 이미지 압축
            selectedPhotoFile = ImageUtils.compressImage(this, imageUri, 1024, 1024, 85);

            Log.d(TAG, "Image selected for weekly diary: " + selectedPhotoFile.getAbsolutePath());

            // 이미지 선택 후 주간 다이어리 생성 진행
            proceedCreateWeeklyDiary(pendingYear, pendingWeekNumber, selectedPhotoFile);

        } catch (IOException e) {
            Log.e(TAG, "Image compression failed: " + e.getMessage());
            Toast.makeText(this, "이미지 처리 실패", Toast.LENGTH_SHORT).show();
        }
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

        pendingYear = currentYear;
        pendingWeekNumber = currentWeekNumber;

        Log.d(TAG, "Creating weekly diary for Year: " + currentYear + ", Week: " + currentWeekNumber);

        // 이미지 선택 Dialog 표시
        showImageSelectionDialog(currentYear, currentWeekNumber);
    }

    /**
     * 이미지 선택 Dialog 표시
     * @param year 연도
     * @param weekNumber 주차
     */
    private void showImageSelectionDialog(int year, int weekNumber) {
        String[] options = {"AI가 자동 생성", "이미지 선택하기"};

        new AlertDialog.Builder(this)
                .setTitle("주간 대표 이미지")
                .setMessage("주간 다이어리에 표시될 이미지를 선택하세요")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // AI 자동 생성
                        checkAndCreateWeeklyDiary(year, weekNumber, null);
                    } else {
                        // 이미지 선택하기
                        openGallery();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 갤러리 열기
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    /**
     * 주간 다이어리 생성 진행
     * @param year 연도
     * @param weekNumber 주차
     * @param photoFile 사진 파일 (nullable)
     */
    private void proceedCreateWeeklyDiary(int year, int weekNumber, File photoFile) {
        checkAndCreateWeeklyDiary(year, weekNumber, photoFile);
    }

    /**
     * 주간 다이어리 생성 전 검증 및 생성
     * @param year 연도
     * @param weekNumber 주차
     * @param photoFile 사진 파일 (nullable)
     */
    private void checkAndCreateWeeklyDiary(int year, int weekNumber, File photoFile) {
        loadingDialog = LoadingDialog.show(this, "주간 다이어리 생성 준비 중...");

        // 1단계: 이미 생성된 주간 다이어리가 있는지 확인
        weeklyDiaryService.getWeeklyDiaryByDate(this, year, weekNumber, new OnApiResponseListener<WeeklyDiary>() {
            @Override
            public void onSuccess(WeeklyDiary existingDiary) {
                // 이미 주간 다이어리가 존재하는 경우
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.d(TAG, "Weekly diary already exists: " + existingDiary.getWeekId());

                new AlertDialog.Builder(WeeklyDiaryListActivity.this)
                        .setTitle("주간 다이어리 존재")
                        .setMessage(year + "년 " + weekNumber + "주차 다이어리가 이미 존재합니다.\n다시 생성하시겠습니까?")
                        .setPositiveButton("재생성", (d, w) -> {
                            createWeeklyDiaryApi(year, weekNumber, photoFile);
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }

            @Override
            public void onError(String error) {
                // 주간 다이어리가 없는 경우 (404 에러) - 정상적으로 생성 진행
                if (error.contains("404") || error.contains("not found")) {
                    Log.d(TAG, "Weekly diary not found, creating new one");
                    createWeeklyDiaryApi(year, weekNumber, photoFile);
                } else {
                    // 네트워크 오류 등 다른 에러
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    Log.e(TAG, "Check weekly diary error: " + error);
                    Toast.makeText(WeeklyDiaryListActivity.this,
                            "주간 다이어리 확인 실패: " + error,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 주간 다이어리 생성 API 호출
     * @param year 연도
     * @param weekNumber 주차
     * @param photoFile 사진 파일 (nullable)
     */
    private void createWeeklyDiaryApi(int year, int weekNumber, File photoFile) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setMessage("주간 다이어리 생성 중...\n(AI 요약 및 제목 생성)");
        } else {
            loadingDialog = LoadingDialog.show(this, "주간 다이어리 생성 중...\n(AI 요약 및 제목 생성)");
        }

        weeklyDiaryService.createWeeklyDiary(this, year, weekNumber, photoFile, new OnApiResponseListener<WeeklyDiary>() {
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
