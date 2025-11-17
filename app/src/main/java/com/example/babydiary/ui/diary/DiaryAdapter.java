package com.example.babydiary.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.dto.Tag;
import com.example.babydiary.databinding.ItemDiaryBinding;
import com.example.babydiary.utils.DateUtils;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<DiaryResponse> diaryList;
    private OnDiaryClickListener listener;

    public interface OnDiaryClickListener {
        void onDiaryClick(DiaryResponse diary);
    }

    public DiaryAdapter(List<DiaryResponse> diaryList, OnDiaryClickListener listener) {
        this.diaryList = diaryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiaryBinding binding = ItemDiaryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DiaryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryResponse diary = diaryList.get(position);
        holder.bind(diary);
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    class DiaryViewHolder extends RecyclerView.ViewHolder {
        private ItemDiaryBinding binding;

        public DiaryViewHolder(ItemDiaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DiaryResponse diary) {
            // 날짜 표시
            binding.tvDate.setText(DateUtils.serverDateToDisplay(diary.getDate()));

            // 설명 표시
            binding.tvDescription.setText(diary.getDescription());

            // 이미지 로딩
            if (diary.getPhotoUrl() != null && !diary.getPhotoUrl().isEmpty()) {
                binding.ivPhoto.setVisibility(View.VISIBLE);
                Glide.with(binding.getRoot().getContext())
                        .load(diary.getPhotoUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(binding.ivPhoto);
            } else {
                binding.ivPhoto.setVisibility(View.GONE);
            }

            // 감정 표시
            if (diary.getEmotion() != null && !diary.getEmotion().isEmpty()) {
                binding.tvEmotion.setVisibility(View.VISIBLE);
                binding.tvEmotion.setText(getEmotionEmoji(diary.getEmotion()));
            } else {
                binding.tvEmotion.setVisibility(View.GONE);
            }

            // 태그 표시
            if (diary.getTags() != null && !diary.getTags().isEmpty()) {
                StringBuilder tagText = new StringBuilder();
                for (Tag tag : diary.getTags()) {
                    if (tagText.length() > 0) tagText.append(" ");
                    tagText.append("#").append(tag.getTagName());
                }
                binding.tvTags.setText(tagText.toString());
                binding.tvTags.setVisibility(View.VISIBLE);
            } else {
                binding.tvTags.setVisibility(View.GONE);
            }

            // 클릭 리스너
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDiaryClick(diary);
                }
            });
        }

        private String getEmotionEmoji(String emotion) {
            switch (emotion.toLowerCase()) {
                case "joy":
                case "기쁨":
                    return "😊";
                case "sadness":
                case "슬픔":
                    return "😢";
                case "anger":
                case "화남":
                    return "😠";
                case "surprise":
                case "놀람":
                    return "😲";
                case "fear":
                case "두려움":
                    return "😨";
                case "neutral":
                case "중립":
                default:
                    return "😐";
            }
        }
    }
}