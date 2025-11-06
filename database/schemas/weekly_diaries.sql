-- Weekly Diaries Table
-- 주간 다이어리 (7일 다이어리 종합)

CREATE TABLE IF NOT EXISTS weekly_diaries (
    week_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    year INTEGER NOT NULL,
    week_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    weekly_summary_text TEXT,
    weekly_image_url VARCHAR(500),
    weekly_title VARCHAR(200),
    user_uploaded_image BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_year_week UNIQUE (user_id, year, week_number)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_weekly_diaries_user_year_week ON weekly_diaries(user_id, year DESC, week_number DESC);
CREATE INDEX IF NOT EXISTS idx_weekly_diaries_created_at ON weekly_diaries(created_at DESC);

-- Trigger for updated_at
CREATE TRIGGER update_weekly_diaries_updated_at BEFORE UPDATE ON weekly_diaries
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
