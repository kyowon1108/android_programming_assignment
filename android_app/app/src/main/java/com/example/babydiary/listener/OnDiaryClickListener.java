package com.example.babydiary.listener;

import com.example.babydiary.model.Diary;

/**
 * 다이어리 아이템 클릭 리스너
 */
public interface OnDiaryClickListener {
    /**
     * 다이어리 아이템 클릭
     * @param diary 클릭된 다이어리
     */
    void onDiaryClick(Diary diary);

    /**
     * 다이어리 삭제 버튼 클릭
     * @param diary 삭제할 다이어리
     */
    void onDeleteClick(Diary diary);
}
