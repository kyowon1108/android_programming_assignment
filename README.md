# 애기 다이어리 & AI 그림동화 앱

## 프로젝트 개요
아이의 일상을 **3줄 다이어리 + 1장 사진**으로 기록하고, **AI(Google Vision, Gemini)**를 통해 동화, 감정 분석, 전문가 의견을 자동 생성하는 완전한 스택 애플리케이션입니다.

## 주요 기능

### 1. 데일리 다이어리
- 3줄 텍스트 + 1장 사진 기록
- **Google Vision API**로 사진 자동 분석
- **Google Gemini API**로:
  - 어린이를 위한 그림동화 생성
  - 감정 분석 (기쁨, 슬픔, 화남, 놀람, 두려움, 평온)
  - 육아 전문가 의견 생성

### 2. 주간 다이어리
- 7일 다이어리 자동 종합
- AI 생성 주간 제목 및 요약
- BGM 재생 (예정)

### 3. 검색 & 필터
- 태그별 검색
- 연도/주차별 필터
- 감정별 필터

### 4. 연속 작성 추적
- Best Streak (최장 연속 기록)
- Current Streak (현재 연속 기록)

---

## 기술 스택

| 계층 | 기술 |
|------|------|
| **프론트엔드** | Android (Java), API 24+ (Android 7.0) |
| **백엔드** | Python FastAPI |
| **데이터베이스** | PostgreSQL |
| **외부 API** | Google Cloud Vision API, Google Gemini API |
| **인증** | JWT (JSON Web Tokens) |

---

## 프로젝트 구조

```
android_programming_assignment/
├── android_app/         # Android 모바일 앱 (Java, API 24+)
├── backend/             # REST API 서버 (Python FastAPI)
├── database/            # PostgreSQL 스크립트
├── docs/                # 프로젝트 문서
└── documentation/       # 개발 가이드 및 템플릿
```

### 상세 구조
```
.
├── android_app/
│   ├── app/
│   │   └── src/
│   │       └── main/
│   │           ├── java/com/example/babydiary/
│   │           │   ├── activity/       # 화면 (7개)
│   │           │   ├── service/        # API 호출 (5개)
│   │           │   ├── database/       # SQLite (4개)
│   │           │   ├── model/          # 데이터 모델 (5개)
│   │           │   ├── adapter/        # 어댑터 (4개)
│   │           │   ├── util/           # 유틸리티 (7개)
│   │           │   ├── listener/       # 리스너 (2개)
│   │           │   └── dialog/         # 다이얼로그 (3개)
│   │           └── res/
│   │               ├── layout/         # XML 레이아웃 (13개)
│   │               ├── drawable/       # 이미지/벡터 (9개)
│   │               └── raw/            # BGM 음악 (4개 mp3)
│   ├── build.gradle
│   └── README.md
│
├── backend/
│   ├── main.py                 # FastAPI 애플리케이션
│   ├── requirements.txt        # Python 의존성
│   ├── .env.example            # 환경 변수 예시
│   ├── app/
│   │   ├── config/
│   │   │   ├── settings.py     # 설정
│   │   │   └── database.py     # DB 연결
│   │   ├── models/
│   │   │   └── schemas.py      # Pydantic 모델
│   │   ├── routes/
│   │   │   ├── auth.py         # 인증
│   │   │   ├── diaries.py      # 다이어리
│   │   │   ├── weekly_diaries.py
│   │   │   └── tags.py
│   │   ├── services/
│   │   │   ├── vision_service.py
│   │   │   └── gemini_service.py
│   │   └── utils/
│   │       ├── auth.py         # JWT
│   │       ├── file_handler.py
│   │       └── date_utils.py
│   ├── uploads/                # 업로드 파일
│   └── README.md
│
├── database/
│   ├── init.sql                # 전체 초기화
│   ├── schemas/
│   │   ├── users.sql
│   │   ├── diaries.sql
│   │   ├── tags.sql
│   │   ├── diary_tags.sql
│   │   └── weekly_diaries.sql
│   └── README.md
│
└── docs/
    ├── API_SPEC.md             # API 명세서
    ├── DATABASE_SCHEMA.md      # DB 스키마 (예정)
    └── ARCHITECTURE.md         # 아키텍처 (예정)
```

---

## 설치 및 실행

### 전체 환경 설정 순서
1. PostgreSQL 설치 및 데이터베이스 생성
2. Backend API 서버 설정 및 실행
3. Android 앱 빌드 및 실행

---

### 1. 데이터베이스 설정

#### PostgreSQL 설치

**macOS**:
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Ubuntu/Debian**:
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Windows**: [공식 사이트](https://www.postgresql.org/download/windows/)에서 설치

#### 데이터베이스 초기화
```bash
# 데이터베이스 생성
createdb baby_diary

# 스키마 초기화
cd database
psql -U postgres -d baby_diary -f init.sql

# 확인
psql -U postgres -d baby_diary -c "\dt"
```

---

### 2. Backend API 서버

#### 설치
```bash
cd backend

# 가상 환경 생성 (macOS/Linux)
python3 -m venv venv
source venv/bin/activate

# 가상 환경 생성 (Windows)
python -m venv venv
venv\Scripts\activate

# 의존성 설치
pip install -r requirements.txt

# 환경 변수 설정
cp .env.example .env
# .env 파일을 편집하여 DB 비밀번호, API 키 등 설정
```

#### Google API 키 설정
1. **Google Vision API**:
   - [Google Cloud Console](https://console.cloud.google.com) 접속
   - 새 프로젝트 생성 → "Cloud Vision API" 활성화
   - API 키 생성 → `.env` 파일에 저장

2. **Google Gemini API**:
   - [Google AI Studio](https://makersuite.google.com/app/apikey) 접속
   - API 키 생성 → `.env` 파일에 저장

#### 서버 실행
```bash
# 개발 모드 (자동 재시작)
uvicorn main:app --reload --host 0.0.0.0 --port 8000

# 또는
python main.py
```

서버 접속:
- API: http://localhost:8000
- API 문서: http://localhost:8000/docs

---

### 3. Android 앱

#### 사전 요구사항
- Android Studio 최신 버전
- JDK 11 이상
- Android SDK 34

#### 빌드 및 실행
```bash
cd android_app

# 빌드
./gradlew build

# 에뮬레이터/기기에 설치
./gradlew installDebug
```

**Android Studio에서**:
1. `android_app` 폴더를 프로젝트로 열기
2. 에뮬레이터 시작 또는 실제 기기 연결
3. Run 버튼 클릭

---

## API 문서

### 주요 엔드포인트

#### 인증
- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/login` - 로그인
- `GET /api/v1/auth/me` - 현재 사용자 정보
- `POST /api/v1/auth/refresh` - 토큰 갱신

#### 다이어리
- `POST /api/v1/diaries` - 다이어리 생성
- `GET /api/v1/diaries` - 다이어리 목록
- `GET /api/v1/diaries/{diary_id}` - 다이어리 상세
- `DELETE /api/v1/diaries/{diary_id}` - 다이어리 삭제

#### 주간 다이어리
- `POST /api/v1/weekly_diaries` - 주간 다이어리 생성
- `GET /api/v1/weekly_diaries` - 주간 목록
- `GET /api/v1/weekly_diaries/{week_id}` - 주간 상세

#### 태그
- `GET /api/v1/tags` - 모든 태그
- `GET /api/v1/tags/category/{category}` - 카테고리별 태그

자세한 API 명세는 [docs/API_SPEC.md](docs/API_SPEC.md) 참고

---

## 개발 가이드

### 커밋 규칙
[documentation/templates/commit-template.md](documentation/templates/commit-template.md) 참고

**기본 형식**:
```
<type>(<scope>): <subject>

<body>

<footer>
```

**예시**:
```
feat(backend): Google Gemini API 통합

- 동화 생성 기능 추가
- 감정 분석 기능 추가
- 전문가 의견 생성 기능 추가

Closes #123
```

### 기능 개발
[documentation/templates/feature-template.md](documentation/templates/feature-template.md) 참고

### 테스트
[documentation/templates/test-template.md](documentation/templates/test-template.md) 참고

---

## 데이터베이스 스키마

### 주요 테이블

#### users
사용자 정보 및 연속 작성 기록
- `user_id`, `email`, `password_hash`, `nickname`
- `best_streak`, `current_streak`, `last_diary_date`

#### diaries
일일 다이어리
- `diary_id`, `user_id`, `date`, `description`, `photo_url`
- `vision_description`, `generated_story`, `expert_comment`, `emotion`
- `year`, `week_number`

#### tags
태그 (기쁨, 슬픔, 성장, 우정, 건강 등)
- `tag_id`, `tag_name`, `tag_category`

#### diary_tags
다이어리-태그 다대다 관계
- `diary_id`, `tag_id`

#### weekly_diaries
주간 다이어리
- `week_id`, `user_id`, `year`, `week_number`
- `start_date`, `end_date`, `weekly_summary_text`, `weekly_title`

---

## 트러블슈팅

### Backend 서버가 시작되지 않음
```bash
# PostgreSQL 서비스 확인
sudo service postgresql status

# 환경 변수 확인
cat backend/.env

# 데이터베이스 연결 테스트
psql -U postgres -d baby_diary
```

### Android 빌드 오류
```bash
# Gradle 캐시 삭제
cd android_app
./gradlew clean

# 의존성 재다운로드
./gradlew --refresh-dependencies
```

### Google API 오류
- API 키가 올바른지 확인
- Google Cloud Console에서 API가 활성화되었는지 확인
- API 할당량 초과 여부 확인

---

## 보안 주의사항
- ⚠️ `.env` 파일은 Git에 커밋하지 않음 (`.gitignore`에 포함됨)
- ⚠️ API 키는 환경 변수로만 관리
- ⚠️ JWT 시크릿 키는 강력하게 설정 (최소 32자)
- ⚠️ 프로덕션 환경에서는 HTTPS 사용 권장

---

## 라이선스
MIT License

---

## 개발자
**kyowon1108**

## 문의
- 이메일: kyowon1108@gmail.com
- GitHub: [kyowon1108](https://github.com/kyowon1108)

---

## 프로젝트 상태
- ✅ Database 스키마 완성
- ✅ Backend API 완성
- ✅ Android 개발 환경 설정 완료
- ⏳ Android 앱 기능 구현 진행 중
- ⏳ 통합 테스트 진행 예정

---

## 추후 개발 계획
- [ ] Android 앱 전체 기능 구현
- [ ] 주간 다이어리 BGM 재생 기능
- [ ] 이미지 생성 AI 통합 (Stable Diffusion 등)
- [ ] 푸시 알림 (매일 작성 리마인더)
- [ ] 데이터 백업 및 복원 기능
- [ ] 다크 모드 지원
- [ ] 다국어 지원 (영어, 일본어)