-- Diaries Table
-- 일일 다이어리 정보 (3줄 텍스트 + 1장 사진)

CREATE TABLE IF NOT EXISTS diaries (
    diary_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    date DATE NOT NULL,
    description TEXT NOT NULL,
    photo_url VARCHAR(500) NOT NULL,
    vision_description TEXT,
    generated_story TEXT,
    expert_comment TEXT,
    emotion VARCHAR(50),
    year INTEGER NOT NULL,
    week_number INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_date UNIQUE (user_id, date)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_diaries_user_date ON diaries(user_id, date DESC);
CREATE INDEX IF NOT EXISTS idx_diaries_user_year_week ON diaries(user_id, year, week_number);
CREATE INDEX IF NOT EXISTS idx_diaries_emotion ON diaries(emotion);
CREATE INDEX IF NOT EXISTS idx_diaries_created_at ON diaries(created_at DESC);

-- Trigger for updated_at
CREATE TRIGGER update_diaries_updated_at BEFORE UPDATE ON diaries
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
