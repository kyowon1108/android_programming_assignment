package com.example.babydiary.ui.weekly;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babydiary.data.api.WeeklyDiaryApi;
import com.example.babydiary.data.dto.WeeklyDiaryResponse;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.FragmentWeeklyDiaryListBinding;
import com.example.babydiary.utils.AuthUtils;
import com.example.babydiary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyDiaryListFragment extends Fragment {

    private FragmentWeeklyDiaryListBinding binding;
    private WeeklyDiaryApi weeklyDiaryApi;
    private WeeklyDiaryAdapter adapter;
    private final List<WeeklyDiaryResponse> weeklyDiaries = new ArrayList<>();
    private final List<Integer> yearOptions = new ArrayList<>();
    private int selectedYear = DateUtils.getCurrentYear();
    private boolean hasInitializedYearSpinner = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeeklyDiaryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weeklyDiaryApi = ApiClient.getClient().create(WeeklyDiaryApi.class);

        setupYearSpinner();
        setupRecyclerView();
        setupSwipeRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the weekly list when returning from detail screen
        // This will pick up any newly generated titles
        if (hasInitializedYearSpinner) {
            loadWeeklyDiaries();
        }
    }

    private void setupYearSpinner() {
        if (!isAdded()) return;

        yearOptions.clear();
        int currentYear = DateUtils.getCurrentYear();
        for (int year = currentYear - 2; year <= currentYear + 1; year++) {
            yearOptions.add(year);
        }
        if (!yearOptions.contains(selectedYear)) {
            selectedYear = currentYear;
        }

        List<String> labels = new ArrayList<>();
        for (Integer year : yearOptions) {
            labels.add(year + "년");
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                labels
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spYear.setAdapter(yearAdapter);

        int initialIndex = yearOptions.indexOf(selectedYear);
        if (initialIndex >= 0) {
            binding.spYear.setSelection(initialIndex);
        }

        binding.spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int year = yearOptions.get(position);
                if (!hasInitializedYearSpinner || selectedYear != year) {
                    selectedYear = year;
                    loadWeeklyDiaries();
                }
                hasInitializedYearSpinner = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ignore
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new WeeklyDiaryAdapter(weeklyDiaries, weeklyDiary -> {
            Intent intent = new Intent(requireContext(), WeeklyDiaryDetailActivity.class);
            intent.putExtra("year", weeklyDiary.getYear());
            intent.putExtra("week_number", weeklyDiary.getWeekNumber());
            startActivity(intent);
        });
        binding.recyclerWeekly.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerWeekly.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadWeeklyDiaries();
            }
        });
    }

    private void loadWeeklyDiaries() {
        showLoading(true);
        weeklyDiaryApi.getWeeklyDiaryList(selectedYear).enqueue(new Callback<List<WeeklyDiaryResponse>>() {
            @Override
            public void onResponse(Call<List<WeeklyDiaryResponse>> call,
                                   Response<List<WeeklyDiaryResponse>> response) {
                showLoading(false);
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    weeklyDiaries.clear();
                    weeklyDiaries.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateEmptyView();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(requireContext());
                } else {
                    Toast.makeText(getContext(),
                            "주간 데이터를 불러오지 못했습니다 (" + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeeklyDiaryResponse>> call, Throwable t) {
                showLoading(false);
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateEmptyView() {
        if (binding == null) return;
        boolean isEmpty = weeklyDiaries.isEmpty();
        binding.tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerWeekly.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showLoading(boolean show) {
        if (binding == null) {
            return;
        }
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
