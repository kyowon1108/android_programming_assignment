-- Baby Diary Database Initialization Script
-- PostgreSQL 데이터베이스 초기화

-- Execute all schema creation scripts in order
\i schemas/users.sql
\i schemas/diaries.sql
\i schemas/tags.sql
\i schemas/diary_tags.sql
\i schemas/weekly_diaries.sql

-- Verify tables were created
SELECT 'Database initialization completed!' as message;
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;
