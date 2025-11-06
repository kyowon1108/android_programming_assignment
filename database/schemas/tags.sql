-- Tags Table
-- 다이어리 분류를 위한 태그

CREATE TABLE IF NOT EXISTS tags (
    tag_id SERIAL PRIMARY KEY,
    tag_name VARCHAR(100) UNIQUE NOT NULL,
    tag_category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for performance
CREATE INDEX IF NOT EXISTS idx_tags_category ON tags(tag_category);

-- Insert default tags
INSERT INTO tags (tag_name, tag_category) VALUES
('기쁨', 'emotion'),
('슬픔', 'emotion'),
('화남', 'emotion'),
('놀람', 'emotion'),
('두려움', 'emotion'),
('평온', 'emotion'),
('성장', 'development'),
('우정', 'relationship'),
('가족', 'relationship'),
('감동', 'emotion'),
('기억', 'memory'),
('건강', 'health'),
('학습', 'development'),
('놀이', 'activity'),
('음식', 'activity')
ON CONFLICT (tag_name) DO NOTHING;
