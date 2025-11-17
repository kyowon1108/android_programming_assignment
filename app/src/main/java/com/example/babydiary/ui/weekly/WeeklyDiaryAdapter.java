package com.example.babydiary.ui.weekly;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.data.dto.WeeklyDiaryResponse;
import com.example.babydiary.databinding.ItemWeeklyDiaryBinding;
import com.example.babydiary.utils.DateUtils;

import java.util.List;

public class WeeklyDiaryAdapter extends RecyclerView.Adapter<WeeklyDiaryAdapter.WeeklyViewHolder> {

    public interface OnWeeklyDiaryClickListener {
        void onWeeklyDiaryClick(WeeklyDiaryResponse weeklyDiary);
    }

    private final List<WeeklyDiaryResponse> weeklyDiaries;
    private final OnWeeklyDiaryClickListener listener;

    public WeeklyDiaryAdapter(List<WeeklyDiaryResponse> weeklyDiaries,
                              OnWeeklyDiaryClickListener listener) {
        this.weeklyDiaries = weeklyDiaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeeklyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeeklyDiaryBinding binding = ItemWeeklyDiaryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new WeeklyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyViewHolder holder, int position) {
        WeeklyDiaryResponse item = weeklyDiaries.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return weeklyDiaries.size();
    }

    class WeeklyViewHolder extends RecyclerView.ViewHolder {
        private final ItemWeeklyDiaryBinding binding;

        WeeklyViewHolder(ItemWeeklyDiaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WeeklyDiaryResponse weeklyDiary) {
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
                Glide.with(binding.getRoot().getContext())
                        .load(weeklyDiary.getWeeklyImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(binding.ivWeeklyImage);
            } else {
                binding.ivWeeklyImage.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWeeklyDiaryClick(weeklyDiary);
                }
            });
        }

        private String formatDate(String date) {
            if (TextUtils.isEmpty(date)) {
                return "-";
            }
            return DateUtils.serverDateToDisplay(date);
        }
    }
}
