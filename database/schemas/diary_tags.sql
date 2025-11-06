-- Diary Tags Table (Many-to-Many relationship)
-- 다이어리와 태그의 관계 테이블

CREATE TABLE IF NOT EXISTS diary_tags (
    diary_id INTEGER NOT NULL REFERENCES diaries(diary_id) ON DELETE CASCADE,
    tag_id INTEGER NOT NULL REFERENCES tags(tag_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (diary_id, tag_id)
);

-- Index for performance
CREATE INDEX IF NOT EXISTS idx_diary_tags_tag_id ON diary_tags(tag_id);
CREATE INDEX IF NOT EXISTS idx_diary_tags_diary_id ON diary_tags(diary_id);
