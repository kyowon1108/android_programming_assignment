package com.example.babydiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babydiary.R;
import com.example.babydiary.model.WeeklyDiary;

import java.util.List;

/**
 * 주간 다이어리 목록 어댑터
 */
public class WeeklyDiaryAdapter extends RecyclerView.Adapter<WeeklyDiaryAdapter.ViewHolder> {

    private final Context context;
    private final List<WeeklyDiary> weeklyDiaries;
    private final OnWeeklyDiaryClickListener listener;

    public interface OnWeeklyDiaryClickListener {
        void onWeeklyDiaryClick(WeeklyDiary weeklyDiary);
    }

    public WeeklyDiaryAdapter(Context context, List<WeeklyDiary> weeklyDiaries, OnWeeklyDiaryClickListener listener) {
        this.context = context;
        this.weeklyDiaries = weeklyDiaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weekly_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeeklyDiary weeklyDiary = weeklyDiaries.get(position);

        // 제목
        holder.tvWeekTitle.setText(weeklyDiary.getYear() + "년 " + weeklyDiary.getWeekNumber() + "주차");

        // 날짜 범위
        holder.tvDateRange.setText(weeklyDiary.getStartDate() + " ~ " + weeklyDiary.getEndDate());

        // 다이어리 개수
        int diaryCount = weeklyDiary.getDiaries() != null ? weeklyDiary.getDiaries().size() : 0;
        holder.tvDiaryCount.setText("일기 " + diaryCount + "개");

        // 클릭 리스너
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWeeklyDiaryClick(weeklyDiary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weeklyDiaries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeekTitle;
        TextView tvDateRange;
        TextView tvDiaryCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeekTitle = itemView.findViewById(R.id.tv_week_title);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvDiaryCount = itemView.findViewById(R.id.tv_diary_count);
        }
    }
}
