package com.example.babydiary.ui.search;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babydiary.data.api.DiaryApi;
import com.example.babydiary.data.api.TagApi;
import com.example.babydiary.data.dto.DiaryListResponse;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.dto.Tag;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.FragmentSearchBinding;
import com.example.babydiary.ui.diary.DiaryAdapter;
import com.example.babydiary.ui.diary.DiaryDetailActivity;
import com.example.babydiary.utils.AuthUtils;
import com.example.babydiary.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private DiaryApi diaryApi;
    private TagApi tagApi;
    private DiaryAdapter diaryAdapter;

    private final List<DiaryResponse> diaryList = new ArrayList<>();
    private final List<Integer> tagIdOptions = new ArrayList<>();
    private final List<String> tagNameOptions = new ArrayList<>();
    private ArrayAdapter<String> tagSpinnerAdapter;

    private static final String[] EMOTION_VALUES = {null, "joy", "sadness", "anger", "surprise", "fear", "neutral"};
    private static final String[] EMOTION_LABELS = {"전체 감정", "기쁨", "슬픔", "분노", "놀람", "두려움", "중립"};

    private String keyword = null;
    private String startDate = null;
    private String endDate = null;
    private String startDatePlaceholder;
    private String endDatePlaceholder;
    private String selectedEmotion = null;
    private Integer selectedTagId = null;

    private static final int LIMIT = 20;
    private int offset = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        diaryApi = ApiClient.getClient().create(DiaryApi.class);
        tagApi = ApiClient.getClient().create(TagApi.class);

        startDatePlaceholder = binding.btnStartDate.getText().toString();
        endDatePlaceholder = binding.btnEndDate.getText().toString();

        setupRecyclerView();
        setupSwipeRefresh();
        setupEmotionSpinner();
        setupTagSpinner();
        setupInputs();

        loadDiaries(true);
    }

    private void setupRecyclerView() {
        diaryAdapter = new DiaryAdapter(diaryList, diary -> {
            Intent intent = new Intent(requireContext(), DiaryDetailActivity.class);
            intent.putExtra("diary_id", diary.getDiaryId());
            startActivity(intent);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvSearchResult.setLayoutManager(layoutManager);
        binding.rvSearchResult.setAdapter(diaryAdapter);
        binding.rvSearchResult.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0 || isLoading || isLastPage) {
                    return;
                }
                int visibleCount = layoutManager.getChildCount();
                int totalCount = layoutManager.getItemCount();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                if (visibleCount + firstVisible >= totalCount - 2) {
                    loadDiaries(false);
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> loadDiaries(true));
    }

    private void setupEmotionSpinner() {
        ArrayAdapter<String> emotionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                EMOTION_LABELS
        );
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spEmotion.setAdapter(emotionAdapter);
        binding.spEmotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEmotion = EMOTION_VALUES[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEmotion = null;
            }
        });
    }

    private void setupTagSpinner() {
        tagIdOptions.clear();
        tagNameOptions.clear();
        tagIdOptions.add(null);
        tagNameOptions.add("전체 태그");

        tagSpinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tagNameOptions
        );
        tagSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTag.setAdapter(tagSpinnerAdapter);
        binding.spTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTagId = tagIdOptions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTagId = null;
            }
        });

        loadTags();
    }

    private void loadTags() {
        tagApi.getTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (!isAdded() || binding == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    for (Tag tag : response.body()) {
                        tagIdOptions.add(tag.getTagId());
                        tagNameOptions.add(tag.getTagName());
                    }
                    tagSpinnerAdapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(requireContext());
                } else {
                    Toast.makeText(getContext(),
                            "태그를 불러올 수 없습니다 (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "태그 로딩 실패: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupInputs() {
        binding.btnSearch.setOnClickListener(v -> performSearch());

        binding.btnStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.btnEndDate.setOnClickListener(v -> showDatePicker(false));

        binding.btnStartDate.setOnLongClickListener(v -> {
            startDate = null;
            binding.btnStartDate.setText(startDatePlaceholder);
            return true;
        });

        binding.btnEndDate.setOnLongClickListener(v -> {
            endDate = null;
            binding.btnEndDate.setText(endDatePlaceholder);
            return true;
        });
    }

    private void performSearch() {
        String input = binding.etKeyword.getText() != null
                ? binding.etKeyword.getText().toString().trim()
                : "";
        keyword = TextUtils.isEmpty(input) ? null : input;
        loadDiaries(true);
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        String targetDate = isStart ? startDate : endDate;
        if (!TextUtils.isEmpty(targetDate)) {
            Date date = DateUtils.parseServerDate(targetDate);
            calendar.setTime(date);
        }

        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date date = calendar.getTime();
                    String serverDate = DateUtils.dateToServerFormat(date);
                    if (isStart) {
                        startDate = serverDate;
                        binding.btnStartDate.setText(DateUtils.serverDateToDisplay(serverDate));
                    } else {
                        endDate = serverDate;
                        binding.btnEndDate.setText(DateUtils.serverDateToDisplay(serverDate));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void loadDiaries(boolean reset) {
        if (!isAdded() || binding == null) return;
        if (isLoading) {
            if (reset) {
                binding.swipeRefresh.setRefreshing(false);
            }
            return;
        }

        if (reset) {
            offset = 0;
            isLastPage = false;
            diaryList.clear();
            diaryAdapter.notifyDataSetChanged();
            updateEmptyView();
        } else if (isLastPage) {
            return;
        }

        isLoading = true;
        showLoading(true);

        Call<DiaryListResponse> call = diaryApi.getDiaries(
                LIMIT,
                offset,
                startDate,
                endDate,
                keyword,
                selectedEmotion,
                selectedTagId
        );

        call.enqueue(new Callback<DiaryListResponse>() {
            @Override
            public void onResponse(Call<DiaryListResponse> call, Response<DiaryListResponse> response) {
                isLoading = false;
                if (!isAdded() || binding == null) return;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    DiaryListResponse data = response.body();
                    List<DiaryResponse> newItems = data.getDiaries();
                    if (newItems != null && !newItems.isEmpty()) {
                        int startPosition = diaryList.size();
                        diaryList.addAll(newItems);
                        diaryAdapter.notifyItemRangeInserted(startPosition, newItems.size());
                        offset += newItems.size();
                    }
                    int total = data.getTotal();
                    isLastPage = diaryList.size() >= total;
                    updateEmptyView();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(requireContext());
                } else {
                    Toast.makeText(getContext(),
                            "검색 실패: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryListResponse> call, Throwable t) {
                isLoading = false;
                if (!isAdded() || binding == null) return;
                showLoading(false);
                Toast.makeText(getContext(),
                        "네트워크 오류: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateEmptyView() {
        boolean isEmpty = diaryList.isEmpty();
        binding.tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvSearchResult.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showLoading(boolean show) {
        if (binding == null) return;
        if (!binding.swipeRefresh.isRefreshing()) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (!show) {
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
