-- Migration: Add missing columns to weekly_diaries table
-- Date: 2025-11-11
-- Issue: Column "weekly_summary_text" does not exist error

-- Problem:
-- Backend API (weekly_diaries.py) expects columns that don't exist in DB:
-- - weekly_summary_text
-- - weekly_image_url
-- - weekly_title
-- - user_uploaded_image

-- Solution: Add missing columns

ALTER TABLE weekly_diaries
ADD COLUMN IF NOT EXISTS weekly_summary_text TEXT,
ADD COLUMN IF NOT EXISTS weekly_image_url VARCHAR(500),
ADD COLUMN IF NOT EXISTS weekly_title VARCHAR(200),
ADD COLUMN IF NOT EXISTS user_uploaded_image BOOLEAN DEFAULT FALSE;

-- Verify:
-- \d weekly_diaries;
-- Expected 12 columns total (8 existing + 4 new)

-- Rollback (if needed):
-- ALTER TABLE weekly_diaries
-- DROP COLUMN IF EXISTS weekly_summary_text,
-- DROP COLUMN IF EXISTS weekly_image_url,
-- DROP COLUMN IF EXISTS weekly_title,
-- DROP COLUMN IF EXISTS user_uploaded_image;
