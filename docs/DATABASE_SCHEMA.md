# Database Schema Documentation

## Overview
PostgreSQL 데이터베이스 스키마 설계 문서입니다.

## Tables

### 1. users
사용자 정보를 저장하는 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | SERIAL | PRIMARY KEY | 사용자 고유 ID |
| email | VARCHAR(255) | UNIQUE, NOT NULL | 이메일 주소 |
| password_hash | VARCHAR(255) | NOT NULL | 암호화된 비밀번호 |
| nickname | VARCHAR(100) | NOT NULL | 사용자 닉네임 |
| profile_image_url | VARCHAR(500) | | 프로필 이미지 URL |
| best_streak | INTEGER | DEFAULT 0 | 최고 연속 작성 일수 |
| current_streak | INTEGER | DEFAULT 0 | 현재 연속 작성 일수 |
| last_diary_date | DATE | | 마지막 다이어리 작성 날짜 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 계정 생성 시간 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 정보 수정 시간 |

### 2. diaries
다이어리 정보를 저장하는 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| diary_id | SERIAL | PRIMARY KEY | 다이어리 고유 ID |
| user_id | INTEGER | FOREIGN KEY REFERENCES users(user_id) | 작성자 ID |
| date | DATE | NOT NULL | 다이어리 날짜 |
| description | TEXT | NOT NULL | 사용자 입력 내용 (3줄) |
| photo_url | VARCHAR(500) | NOT NULL | 사진 URL |
| vision_description | TEXT | | Vision API 분석 결과 |
| generated_story | TEXT | | Gemini 생성 동화 |
| expert_comment | TEXT | | 전문가 의견 |
| emotion | VARCHAR(50) | | 감정 분석 결과 |
| year | INTEGER | NOT NULL | 연도 |
| week_number | INTEGER | NOT NULL | 주차 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 생성 시간 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 수정 시간 |

**Indexes:**
- `idx_diaries_user_date` ON (user_id, date)
- `idx_diaries_user_year_week` ON (user_id, year, week_number)
- `idx_diaries_emotion` ON (emotion)

**Constraints:**
- `unique_user_date` UNIQUE (user_id, date) - 하루에 하나의 다이어리만 작성 가능

### 3. tags
태그 마스터 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| tag_id | SERIAL | PRIMARY KEY | 태그 고유 ID |
| tag_name | VARCHAR(100) | UNIQUE, NOT NULL | 태그명 |
| tag_category | VARCHAR(50) | | 태그 카테고리 |

**Initial Data:**
- 감정: 기쁨, 슬픔, 화남, 놀람, 두려움
- 발달: 성장
- 관계: 우정
- 기타: 감동, 기억, 건강

### 4. diary_tags
다이어리-태그 관계 테이블 (M:N)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| diary_id | INTEGER | FOREIGN KEY REFERENCES diaries(diary_id) | 다이어리 ID |
| tag_id | INTEGER | FOREIGN KEY REFERENCES tags(tag_id) | 태그 ID |

**Constraints:**
- PRIMARY KEY (diary_id, tag_id)
- ON DELETE CASCADE

**Indexes:**
- `idx_diary_tags_tag_id` ON (tag_id)

### 5. weekly_diaries
주간 다이어리 테이블

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| week_id | SERIAL | PRIMARY KEY | 주간 다이어리 고유 ID |
| user_id | INTEGER | FOREIGN KEY REFERENCES users(user_id) | 사용자 ID |
| year | INTEGER | NOT NULL | 연도 |
| week_number | INTEGER | NOT NULL | 주차 번호 |
| start_date | DATE | NOT NULL | 주 시작일 |
| end_date | DATE | NOT NULL | 주 종료일 |
| weekly_summary_text | TEXT | | AI 생성 주간 요약 |
| weekly_image_url | VARCHAR(500) | | 대표 이미지 URL |
| weekly_title | VARCHAR(200) | | AI 생성 주간 제목 |
| user_uploaded_image | BOOLEAN | DEFAULT FALSE | 사용자 업로드 여부 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 생성 시간 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 수정 시간 |

**Constraints:**
- UNIQUE (user_id, year, week_number)

**Indexes:**
- `idx_weekly_diaries_user_year_week` ON (user_id, year, week_number)

## Relationships

```
users (1) ──── (N) diaries
users (1) ──── (N) weekly_diaries
diaries (M) ──── (N) tags (through diary_tags)
```

## Performance Considerations

1. **Indexing Strategy**
   - 자주 조회되는 컬럼에 인덱스 생성
   - 복합 인덱스로 쿼리 성능 최적화

2. **Partitioning** (향후 고려)
   - diaries 테이블을 year별로 파티셔닝
   - 데이터 증가시 성능 유지

3. **Caching Strategy**
   - 자주 조회되는 태그 목록 캐싱
   - 주간 다이어리 요약 캐싱

## Migration History

- v1.0.0: 초기 스키마 생성
- v1.0.1: last_diary_date 컬럼 추가 (streak 계산용)

## Backup Strategy

1. **Daily Backup**
   ```bash
   pg_dump -U postgres baby_diary > backup_$(date +%Y%m%d).sql
   ```

2. **Restore**
   ```bash
   psql -U postgres baby_diary < backup_20250107.sql
   ```

## Security Considerations

1. **Password Hashing**: bcrypt 사용
2. **SQL Injection Prevention**: Parameterized queries
3. **Access Control**: Row-level security for multi-tenant