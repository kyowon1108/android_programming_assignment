-- Baby Diary Database Schema
-- PostgreSQL 14+
-- Created: 2025-11-11
-- Updated: 2025-11-11

-- ==================== WEEKLY DIARIES TABLE ====================

CREATE TABLE IF NOT EXISTS weekly_diaries (
    week_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    year INTEGER NOT NULL,
    week_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    -- AI-generated content
    weekly_summary_text TEXT,
    weekly_title VARCHAR(200),

    -- Images
    weekly_image_url VARCHAR(500),
    user_uploaded_image BOOLEAN DEFAULT FALSE,

    -- Timestamps
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT unique_user_year_week UNIQUE (user_id, year, week_number),
    CONSTRAINT weekly_diaries_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_weekly_diaries_user_id ON weekly_diaries(user_id);
CREATE INDEX IF NOT EXISTS idx_weekly_diaries_year_week ON weekly_diaries(year, week_number);

-- Comments
COMMENT ON TABLE weekly_diaries IS 'Weekly diary summaries generated from daily diaries';
COMMENT ON COLUMN weekly_diaries.weekly_summary_text IS 'AI-generated summary of all diaries in the week';
COMMENT ON COLUMN weekly_diaries.weekly_title IS 'AI-generated title for the week';
COMMENT ON COLUMN weekly_diaries.weekly_image_url IS 'URL of generated or uploaded image';
COMMENT ON COLUMN weekly_diaries.user_uploaded_image IS 'True if user uploaded custom image, false if AI-generated';
