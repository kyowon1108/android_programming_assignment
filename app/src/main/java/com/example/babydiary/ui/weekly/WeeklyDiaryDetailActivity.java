package com.example.babydiary.ui.weekly;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.data.api.WeeklyDiaryApi;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.dto.WeeklyDiaryResponse;
import com.example.babydiary.data.dto.WeeklyDiaryWithDiariesResponse;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.ActivityWeeklyDiaryDetailBinding;
import com.example.babydiary.ui.diary.DiaryAdapter;
import com.example.babydiary.ui.diary.DiaryDetailActivity;
import com.example.babydiary.utils.AuthUtils;
import com.example.babydiary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyDiaryDetailActivity extends AppCompatActivity {

    private ActivityWeeklyDiaryDetailBinding binding;
    private WeeklyDiaryApi weeklyDiaryApi;
    private DiaryAdapter diaryAdapter;
    private final List<DiaryResponse> diaryList = new ArrayList<>();
    private int year;
    private int weekNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        weeklyDiaryApi = ApiClient.getClient().create(WeeklyDiaryApi.class);

        Intent intent = getIntent();
        year = intent.getIntExtra("year", -1);
        weekNumber = intent.getIntExtra("week_number", -1);

        if (year == -1 || weekNumber == -1) {
            Toast.makeText(this, "주차 정보를 불러올 수 없습니다", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupToolbar();
        setupRecyclerView();
        loadWeeklyDetail();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        diaryAdapter = new DiaryAdapter(diaryList, diary -> {
            Intent intent = new Intent(WeeklyDiaryDetailActivity.this, DiaryDetailActivity.class);
            intent.putExtra("diary_id", diary.getDiaryId());
            startActivity(intent);
        });
        binding.recyclerDiaries.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDiaries.setAdapter(diaryAdapter);
    }

    private void loadWeeklyDetail() {
        showLoading(true);
        weeklyDiaryApi.getWeeklyDiaryDetails(year, weekNumber)
                .enqueue(new Callback<WeeklyDiaryWithDiariesResponse>() {
                    @Override
                    public void onResponse(Call<WeeklyDiaryWithDiariesResponse> call,
                                           Response<WeeklyDiaryWithDiariesResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            WeeklyDiaryWithDiariesResponse data = response.body();
                            if (data.getWeeklyDiary() != null) {
                                displayWeeklyInfo(data.getWeeklyDiary());
                            } else {
                                showSummaryCard(false);
                            }
                            updateDiaries(data.getDiaries());
                        } else if (response.code() == 401) {
                            AuthUtils.handleUnauthorized(WeeklyDiaryDetailActivity.this);
                        } else if (response.code() == 404) {
                            Toast.makeText(WeeklyDiaryDetailActivity.this,
                                    "해당 주차의 요약이 없습니다",
                                    Toast.LENGTH_LONG).show();
                            showSummaryCard(false);
                            updateDiaries(new ArrayList<>());
                            binding.tvEmpty.setText("이번 주에 작성된 일기가 없습니다");
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(WeeklyDiaryDetailActivity.this,
                                    "주간 상세를 불러오지 못했습니다 (" + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeeklyDiaryWithDiariesResponse> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(WeeklyDiaryDetailActivity.this,
                                "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void displayWeeklyInfo(WeeklyDiaryResponse weeklyDiary) {
        showSummaryCard(true);
        binding.tvWeekTitle.setText(
                weeklyDiary.getYear() + "년 " + weeklyDiary.getWeekNumber() + "주차");
        String dateRange = formatDate(weeklyDiary.getStartDate())
                + " - " + formatDate(weeklyDiary.getEndDate());
        binding.tvDateRange.setText(dateRange);

        if (!TextUtils.isEmpty(weeklyDiary.getWeeklyTitle())) {
            binding.tvWeeklyTitle.setText(weeklyDiary.getWeeklyTitle());
            binding.tvWeeklyTitle.setVisibility(View.VISIBLE);
        } else {
            binding.tvWeeklyTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(weeklyDiary.getWeeklySummaryText())) {
            binding.tvWeeklySummary.setText(weeklyDiary.getWeeklySummaryText());
            binding.tvWeeklySummary.setVisibility(View.VISIBLE);
        } else {
            binding.tvWeeklySummary.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(weeklyDiary.getWeeklyImageUrl())) {
            binding.ivWeeklyImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(weeklyDiary.getWeeklyImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(binding.ivWeeklyImage);
        } else {
            binding.ivWeeklyImage.setVisibility(View.GONE);
        }
    }

    private void updateDiaries(List<DiaryResponse> diaries) {
        diaryList.clear();
        if (diaries != null && !diaries.isEmpty()) {
            diaryList.addAll(diaries);
        }
        diaryAdapter.notifyDataSetChanged();
        boolean isEmpty = diaryList.isEmpty();
        binding.tvEmpty.setText("해당 주차의 일기가 없습니다");
        binding.tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerDiaries.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showSummaryCard(boolean show) {
        binding.cardSummary.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.tvDiariesTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String formatDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return "-";
        }
        return DateUtils.serverDateToDisplay(date);
    }
}
