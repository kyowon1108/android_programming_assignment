package com.example.babydiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babydiary.R;
import com.example.babydiary.listener.OnDiaryClickListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.util.DateUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 다이어리 목록 어댑터
 */
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private final Context context;
    private final List<Diary> diaries;
    private final OnDiaryClickListener listener;

    public DiaryAdapter(Context context, List<Diary> diaries, OnDiaryClickListener listener) {
        this.context = context;
        this.diaries = diaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diary diary = diaries.get(position);

        // 날짜
        holder.tvDate.setText(DateUtils.formatDateKorean(diary.getDate()));

        // 설명 (미리보기)
        String description = diary.getDescription();
        if (description.length() > 50) {
            description = description.substring(0, 50) + "...";
        }
        holder.tvDescription.setText(description);

        // 사진
        String photoUrl = diary.getFullPhotoUrl();
        if (photoUrl != null) {
            Glide.with(context)
                    .load(photoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_image_placeholder);
        }

        // 감정 표시
        if (diary.getEmotion() != null && !diary.getEmotion().isEmpty()) {
            holder.tvEmotion.setText(diary.getEmotion());
            holder.tvEmotion.setVisibility(View.VISIBLE);
        } else {
            holder.tvEmotion.setVisibility(View.GONE);
        }

        // 클릭 리스너
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDiaryClick(diary);
            }
        });

        // 삭제 버튼
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDiaryDelete(diary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvDate;
        TextView tvDescription;
        TextView tvEmotion;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvEmotion = itemView.findViewById(R.id.tv_emotion);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
