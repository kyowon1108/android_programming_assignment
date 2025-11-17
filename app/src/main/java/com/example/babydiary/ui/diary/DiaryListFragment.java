package com.example.babydiary.ui.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babydiary.data.api.DiaryApi;
import com.example.babydiary.data.dto.DiaryListResponse;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.FragmentDiaryListBinding;
import com.example.babydiary.utils.AuthUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryListFragment extends Fragment {

    private FragmentDiaryListBinding binding;
    private DiaryApi diaryApi;
    private DiaryAdapter adapter;
    private List<DiaryResponse> diaryList = new ArrayList<>();

    private int currentOffset = 0;
    private static final int LIMIT = 10;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDiaryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // API 초기화
        diaryApi = ApiClient.getClient().create(DiaryApi.class);

        setupRecyclerView();
        setupSwipeRefresh();
        loadDiaries(false);
    }

    private void setupRecyclerView() {
        adapter = new DiaryAdapter(diaryList, diary -> {
            // 다이어리 상세 화면으로 이동
            Intent intent = new Intent(getContext(), DiaryDetailActivity.class);
            intent.putExtra("diary_id", diary.getDiaryId());
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        // 무한 스크롤 구현
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (totalItemCount <= (lastVisibleItem + 2)) {
                        loadDiaries(true);
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    public void refreshData() {
        currentOffset = 0;
        hasMoreData = true;
        diaryList.clear();
        adapter.notifyDataSetChanged();
        loadDiaries(false);
    }

    private void loadDiaries(boolean isLoadMore) {
        if (isLoading) return;

        isLoading = true;
        if (!isLoadMore) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        diaryApi.getDiaries(LIMIT, currentOffset, null, null, null, null, null)
                .enqueue(new Callback<DiaryListResponse>() {
                    @Override
                    public void onResponse(Call<DiaryListResponse> call, Response<DiaryListResponse> response) {
                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            DiaryListResponse data = response.body();
                            List<DiaryResponse> newDiaries = data.getDiaries();

                            if (newDiaries != null && !newDiaries.isEmpty()) {
                                int startPosition = diaryList.size();
                                diaryList.addAll(newDiaries);
                                adapter.notifyItemRangeInserted(startPosition, newDiaries.size());

                                currentOffset += newDiaries.size();
                                hasMoreData = newDiaries.size() >= LIMIT;
                            } else {
                                hasMoreData = false;
                            }

                            // 데이터가 없을 때 empty view 표시
                            if (diaryList.isEmpty()) {
                                binding.tvEmpty.setVisibility(View.VISIBLE);
                                binding.recyclerView.setVisibility(View.GONE);
                            } else {
                                binding.tvEmpty.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.VISIBLE);
                            }
                        } else if (response.code() == 401) {
                            AuthUtils.handleUnauthorized(requireContext());
                        } else {
                            Toast.makeText(getContext(), "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryListResponse> call, Throwable t) {
                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 화면이 다시 보일 때 데이터 새로고침
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
