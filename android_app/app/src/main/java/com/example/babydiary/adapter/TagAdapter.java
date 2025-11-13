package com.example.babydiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babydiary.R;
import com.example.babydiary.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 태그 목록 어댑터 - 선택 상태 관리 기능 추가
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private final Context context;
    private final List<Tag> tags;
    private final Set<Integer> selectedTagIds;
    private final OnTagSelectionListener listener;

    public interface OnTagSelectionListener {
        void onTagSelectionChanged(List<Tag> selectedTags);
    }

    public TagAdapter(Context context, List<Tag> tags, OnTagSelectionListener listener) {
        this.context = context;
        this.tags = tags;
        this.selectedTagIds = new HashSet<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tags.get(position);
        boolean isSelected = selectedTagIds.contains(tag.getTagId());

        holder.tvTagName.setText(tag.getName());

        // 선택 상태에 따라 스타일 변경
        if (isSelected) {
            holder.tvTagName.setBackgroundResource(R.drawable.tag_button_selected);
            holder.tvTagName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.tvTagName.setBackgroundResource(R.drawable.tag_button_unselected);
            holder.tvTagName.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }

        // 클릭 리스너
        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                // 선택 해제
                selectedTagIds.remove(tag.getTagId());
            } else {
                // 선택
                selectedTagIds.add(tag.getTagId());
            }

            // UI 갱신
            notifyItemChanged(position);

            // 리스너 호출
            if (listener != null) {
                listener.onTagSelectionChanged(getSelectedTags());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    /**
     * 선택된 태그 목록 반환
     */
    public List<Tag> getSelectedTags() {
        List<Tag> selected = new ArrayList<>();
        for (Tag tag : tags) {
            if (selectedTagIds.contains(tag.getTagId())) {
                selected.add(tag);
            }
        }
        return selected;
    }

    /**
     * 선택된 태그 ID 목록 반환
     */
    public List<Integer> getSelectedTagIds() {
        return new ArrayList<>(selectedTagIds);
    }

    /**
     * 특정 태그 선택 해제
     */
    public void deselectTag(int tagId) {
        if (selectedTagIds.contains(tagId)) {
            selectedTagIds.remove(tagId);
            notifyDataSetChanged();
            if (listener != null) {
                listener.onTagSelectionChanged(getSelectedTags());
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tv_tag_name);
        }
    }
}