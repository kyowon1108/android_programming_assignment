package com.example.babydiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babydiary.R;
import com.example.babydiary.model.Tag;

import java.util.List;

/**
 * 태그 목록 어댑터
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private final Context context;
    private final List<Tag> tags;
    private final OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    public TagAdapter(Context context, List<Tag> tags, OnTagClickListener listener) {
        this.context = context;
        this.tags = tags;
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

        holder.tvTagName.setText(tag.getName());

        // 클릭 리스너
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTagClick(tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tv_tag_name);
        }
    }
}
