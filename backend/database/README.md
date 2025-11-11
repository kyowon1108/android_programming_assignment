# Database Migrations

## Overview

이 디렉토리는 Baby Diary 프로젝트의 PostgreSQL 데이터베이스 스키마 및 마이그레이션을 관리합니다.

## Database Info

- **Database Name**: `baby_diary`
- **PostgreSQL Version**: 14+
- **Owner**: `kapr` (로컬 개발 환경)

## Schema Files

- `schema.sql`: 전체 데이터베이스 스키마 정의
- `migrations/`: 순차적 마이그레이션 파일

## Migration History

### 001_add_weekly_diary_columns.sql (2025-11-11)

**문제**: Backend API 호출 시 `column "weekly_summary_text" does not exist` 에러 발생

**원인**: `weekly_diaries` 테이블에 Backend 코드에서 요구하는 컬럼이 누락됨
- `weekly_summary_text` (TEXT)
- `weekly_image_url` (VARCHAR(500))
- `weekly_title` (VARCHAR(200))
- `user_uploaded_image` (BOOLEAN)

**해결**: 누락된 4개 컬럼 추가

**실행 방법**:
```bash
psql -d baby_diary -f database/migrations/001_add_weekly_diary_columns.sql
```

**검증**:
```sql
\d weekly_diaries;
-- 총 12개 컬럼 확인 (기존 8개 + 신규 4개)
```

## How to Run Migrations

### 1. 데이터베이스 연결 확인
```bash
psql -l
# baby_diary 데이터베이스가 있는지 확인
```

### 2. 마이그레이션 실행
```bash
# 특정 마이그레이션 실행
psql -d baby_diary -f database/migrations/001_add_weekly_diary_columns.sql

# 또는 psql 내에서
psql -d baby_diary
\i database/migrations/001_add_weekly_diary_columns.sql
```

### 3. 테이블 구조 확인
```sql
\d weekly_diaries;
```

### 4. 데이터 확인
```sql
SELECT * FROM weekly_diaries LIMIT 5;
```

## Rollback

각 마이그레이션 파일 하단에 Rollback SQL이 포함되어 있습니다.

예시:
```sql
-- Rollback for 001_add_weekly_diary_columns.sql
ALTER TABLE weekly_diaries
DROP COLUMN IF EXISTS weekly_summary_text,
DROP COLUMN IF EXISTS weekly_image_url,
DROP COLUMN IF EXISTS weekly_title,
DROP COLUMN IF EXISTS user_uploaded_image;
```

## Best Practices

1. **마이그레이션 순서 유지**: 파일명은 `001_`, `002_` 등 숫자 접두사 사용
2. **테스트 환경 먼저**: 로컬 DB에서 먼저 테스트 후 프로덕션 적용
3. **백업**: 중요한 마이그레이션 전 데이터베이스 백업
   ```bash
   pg_dump -d baby_diary > backup_$(date +%Y%m%d_%H%M%S).sql
   ```
4. **Rollback 준비**: 모든 마이그레이션에 Rollback SQL 포함

## Troubleshooting

### Connection Error
```
psql: error: connection to server on socket "/tmp/.s.PGSQL.5432" failed
```
**해결**: PostgreSQL 서버 시작
```bash
brew services start postgresql@14
```

### Permission Denied
```
ERROR: permission denied for table weekly_diaries
```
**해결**: 올바른 유저로 접속
```bash
psql -U kapr -d baby_diary
```

### Column Already Exists
```
ERROR: column "weekly_summary_text" already exists
```
**해결**: `IF NOT EXISTS` 사용 또는 이미 적용된 마이그레이션 스킵
