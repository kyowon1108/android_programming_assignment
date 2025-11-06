# Database Scripts

## 개요
PostgreSQL 데이터베이스 초기화 및 스키마 관리 스크립트

## 환경 설정

### PostgreSQL 설치

#### macOS
```bash
brew install postgresql@15
brew services start postgresql@15
```

#### Ubuntu/Debian
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

#### Windows
[https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/) 에서 설치

## 초기화

### 1. 데이터베이스 생성
```bash
# PostgreSQL 서비스 시작
sudo service postgresql start

# 데이터베이스 생성
createdb baby_diary

# 또는 psql로 생성
psql -U postgres
CREATE DATABASE baby_diary;
\q
```

### 2. 스키마 초기화
```bash
# 모든 테이블 생성 (init.sql 실행)
psql -U postgres -d baby_diary -f init.sql

# 개별 테이블 생성 (필요시)
psql -U postgres -d baby_diary -f schemas/users.sql
psql -U postgres -d baby_diary -f schemas/diaries.sql
psql -U postgres -d baby_diary -f schemas/tags.sql
psql -U postgres -d baby_diary -f schemas/diary_tags.sql
psql -U postgres -d baby_diary -f schemas/weekly_diaries.sql
```

### 3. 확인
```bash
# 테이블 목록 확인
psql -U postgres -d baby_diary -c "\dt"

# 테이블 구조 확인
psql -U postgres -d baby_diary -c "\d users"
psql -U postgres -d baby_diary -c "\d diaries"

# 데이터 확인
psql -U postgres -d baby_diary -c "SELECT * FROM tags;"
```

## 테이블 구조

### users
사용자 정보 및 연속 작성 기록 관리

| 컬럼 | 타입 | 설명 |
|------|------|------|
| user_id | SERIAL | 사용자 ID (PK) |
| email | VARCHAR(255) | 이메일 (고유) |
| password_hash | VARCHAR(255) | 비밀번호 해시 |
| nickname | VARCHAR(100) | 닉네임 |
| profile_image_url | VARCHAR(500) | 프로필 이미지 URL |
| best_streak | INTEGER | 최장 연속 작성 기록 |
| current_streak | INTEGER | 현재 연속 작성 기록 |
| last_diary_date | DATE | 마지막 다이어리 작성 날짜 |
| created_at | TIMESTAMP | 생성 시각 |
| updated_at | TIMESTAMP | 수정 시각 |

### diaries
일일 다이어리 정보 (3줄 텍스트 + 1장 사진)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| diary_id | SERIAL | 다이어리 ID (PK) |
| user_id | INTEGER | 사용자 ID (FK) |
| date | DATE | 작성 날짜 |
| description | TEXT | 사용자 입력 텍스트 (3줄) |
| photo_url | VARCHAR(500) | 사진 URL |
| vision_description | TEXT | Vision API 결과 |
| generated_story | TEXT | Gemini 생성 동화 |
| expert_comment | TEXT | 전문가 의견 |
| emotion | VARCHAR(50) | 감정 분석 결과 |
| year | INTEGER | 연도 |
| week_number | INTEGER | 주차 |
| created_at | TIMESTAMP | 생성 시각 |
| updated_at | TIMESTAMP | 수정 시각 |

**제약조건**: (user_id, date) 조합은 고유

### tags
다이어리 분류를 위한 태그

| 컬럼 | 타입 | 설명 |
|------|------|------|
| tag_id | SERIAL | 태그 ID (PK) |
| tag_name | VARCHAR(100) | 태그명 (고유) |
| tag_category | VARCHAR(50) | 카테고리 |
| created_at | TIMESTAMP | 생성 시각 |

**기본 태그**: 기쁨, 슬픔, 화남, 놀람, 두려움, 평온, 성장, 우정, 가족, 감동, 기억, 건강, 학습, 놀이, 음식

### diary_tags
다이어리와 태그의 다대다 관계

| 컬럼 | 타입 | 설명 |
|------|------|------|
| diary_id | INTEGER | 다이어리 ID (FK, PK) |
| tag_id | INTEGER | 태그 ID (FK, PK) |
| created_at | TIMESTAMP | 생성 시각 |

### weekly_diaries
주간 다이어리 (7일 다이어리 종합)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| week_id | SERIAL | 주간 다이어리 ID (PK) |
| user_id | INTEGER | 사용자 ID (FK) |
| year | INTEGER | 연도 |
| week_number | INTEGER | 주차 |
| start_date | DATE | 시작 날짜 |
| end_date | DATE | 종료 날짜 |
| weekly_summary_text | TEXT | 주간 요약 |
| weekly_image_url | VARCHAR(500) | 주간 대표 이미지 |
| weekly_title | VARCHAR(200) | 주간 제목 |
| user_uploaded_image | BOOLEAN | 사용자 업로드 여부 |
| created_at | TIMESTAMP | 생성 시각 |
| updated_at | TIMESTAMP | 수정 시각 |

**제약조건**: (user_id, year, week_number) 조합은 고유

## 백업 및 복구

### 전체 백업
```bash
pg_dump -U postgres baby_diary > backup_$(date +%Y%m%d).sql
```

### 테이블별 백업
```bash
pg_dump -U postgres -t diaries baby_diary > diaries_backup.sql
```

### 복구
```bash
psql -U postgres baby_diary < backup_20250106.sql
```

## 데이터베이스 삭제 및 재생성

### 주의: 모든 데이터가 삭제됩니다!
```bash
# 데이터베이스 삭제
dropdb baby_diary

# 재생성 및 초기화
createdb baby_diary
psql -U postgres -d baby_diary -f init.sql
```

## 일반적인 쿼리

### 사용자별 다이어리 개수
```sql
SELECT u.nickname, COUNT(d.diary_id) as diary_count
FROM users u
LEFT JOIN diaries d ON u.user_id = d.user_id
GROUP BY u.user_id, u.nickname
ORDER BY diary_count DESC;
```

### 감정별 다이어리 통계
```sql
SELECT emotion, COUNT(*) as count
FROM diaries
WHERE emotion IS NOT NULL
GROUP BY emotion
ORDER BY count DESC;
```

### 주차별 다이어리 개수
```sql
SELECT year, week_number, COUNT(*) as diary_count
FROM diaries
GROUP BY year, week_number
ORDER BY year DESC, week_number DESC;
```

## 문제 해결

### 연결 오류
```bash
# PostgreSQL 서비스 상태 확인
sudo service postgresql status

# 재시작
sudo service postgresql restart
```

### 권한 오류
```bash
# PostgreSQL 사용자 권한 확인
psql -U postgres
\du

# 권한 부여
GRANT ALL PRIVILEGES ON DATABASE baby_diary TO your_username;
```

## 라이선스
MIT
