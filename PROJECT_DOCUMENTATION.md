# Baby Diary - 완전한 프로젝트 구현 현황

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [데이터베이스 스키마](#데이터베이스-스키마)
4. [Backend API 엔드포인트](#backend-api-엔드포인트)
5. [Android 앱 구조](#android-앱-구조)
6. [주요 기능 구현](#주요-기능-구현)
7. [AI 통합](#ai-통합)
8. [네트워크 설정](#네트워크-설정)
9. [최근 구현 기능](#최근-구현-기능)

---

## 프로젝트 개요

**프로젝트 이름**: Baby Diary (아기 일기장)
**목적**: 아기의 성장 과정을 기록하고 AI가 동화와 전문가 조언을 생성하는 모바일 애플리케이션
**완성도**: 100% (8개 Activity 모두 구현 완료)

### 주요 특징
- 📸 사진 업로드 및 Vision API 자동 분석
- 🤖 Gemini AI 동화 자동 생성
- 👶 전문가 육아 조언 제공
- 📅 주간 다이어리 AI 요약
- 🏷️ 태그 시스템 (감정, 활동, 관계 등)
- 🔥 연속 기록 Streak 시스템

---

## 기술 스택

### Frontend (Android)
```
- Language: Java
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Build Tool: Gradle 8.x
```

**주요 라이브러리:**
- **Glide** 4.15.1: 이미지 로딩
- **Gson** 2.10.1: JSON 직렬화
- **RecyclerView**: 목록 UI
- **CardView**: 카드 UI
- **SwipeRefreshLayout**: Pull-to-Refresh

### Backend
```python
- Framework: FastAPI 0.104.1
- Language: Python 3.11+
- ASGI Server: Uvicorn
```

**주요 라이브러리:**
- **psycopg2**: PostgreSQL 드라이버
- **bcrypt**: 비밀번호 해싱
- **PyJWT**: JWT 토큰 인증
- **httpx**: 비동기 HTTP 클라이언트
- **python-multipart**: 파일 업로드 처리

### Database
```
- DBMS: PostgreSQL 14+
- Total Tables: 5
  - users (사용자)
  - diaries (일일 다이어리)
  - tags (태그)
  - diary_tags (다이어리-태그 관계)
  - weekly_diaries (주간 다이어리)
```

### AI Services
- **Google Gemini API**: 동화 생성, 감정 분석, 전문가 의견
- **Google Vision API**: 이미지 분석 및 설명 생성

---

## 데이터베이스 스키마

### 1. users (사용자 테이블)

```sql
CREATE TABLE users (
    user_id           SERIAL PRIMARY KEY,
    email             VARCHAR(255) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    nickname          VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500),
    best_streak       INTEGER DEFAULT 0,
    current_streak    INTEGER DEFAULT 0,
    last_diary_date   DATE,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
```

**컬럼 설명:**
- `user_id`: 사용자 고유 ID (PK)
- `email`: 이메일 (로그인 ID, UNIQUE)
- `password_hash`: bcrypt 해시 비밀번호
- `nickname`: 표시 이름
- `best_streak`: 최고 연속 기록일
- `current_streak`: 현재 연속 기록일
- `last_diary_date`: 마지막 다이어리 작성일

### 2. diaries (일일 다이어리 테이블)

```sql
CREATE TABLE diaries (
    diary_id           SERIAL PRIMARY KEY,
    user_id            INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    date               DATE NOT NULL,
    description        TEXT NOT NULL,
    photo_url          VARCHAR(500) NOT NULL,
    vision_description TEXT,
    generated_story    TEXT,
    expert_comment     TEXT,
    emotion            VARCHAR(50),
    year               INTEGER NOT NULL,
    week_number        INTEGER NOT NULL,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_user_date UNIQUE (user_id, date)
);

-- Indexes
CREATE INDEX idx_diaries_user_id ON diaries(user_id);
CREATE INDEX idx_diaries_date ON diaries(date);
CREATE INDEX idx_diaries_year_week ON diaries(year, week_number);
```

**컬럼 설명:**
- `diary_id`: 다이어리 고유 ID (PK)
- `user_id`: 작성자 ID (FK)
- `date`: 다이어리 날짜 (하루 1개 제약)
- `description`: 사용자 작성 내용
- `photo_url`: 업로드 이미지 경로
- `vision_description`: Vision API 분석 결과
- `generated_story`: Gemini AI 생성 동화
- `expert_comment`: Gemini AI 전문가 의견
- `emotion`: 감정 (joy, sadness, anger, surprise, fear, neutral)
- `year`, `week_number`: ISO 주차 계산 결과

### 3. tags (태그 테이블)

```sql
CREATE TABLE tags (
    tag_id       SERIAL PRIMARY KEY,
    tag_name     VARCHAR(100) NOT NULL UNIQUE,
    tag_category VARCHAR(50) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_tags_category ON tags(tag_category);
```

**기본 태그 데이터 (15개):**

| Category     | Tags |
|--------------|------|
| emotion      | 기쁨, 슬픔, 화남, 놀람, 두려움, 평온 |
| development  | 성장 |
| relationship | 우정, 가족 |
| health       | 건강 |
| activity     | 놀이, 학습, 외출 |
| daily        | 식사, 수면 |

### 4. diary_tags (다이어리-태그 관계 테이블)

```sql
CREATE TABLE diary_tags (
    diary_id INTEGER NOT NULL REFERENCES diaries(diary_id) ON DELETE CASCADE,
    tag_id   INTEGER NOT NULL REFERENCES tags(tag_id) ON DELETE CASCADE,

    PRIMARY KEY (diary_id, tag_id)
);
```

**다대다 관계:** 하나의 다이어리에 여러 태그, 하나의 태그가 여러 다이어리에 연결

### 5. weekly_diaries (주간 다이어리 테이블)

```sql
CREATE TABLE weekly_diaries (
    week_id             SERIAL PRIMARY KEY,
    user_id             INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    year                INTEGER NOT NULL,
    week_number         INTEGER NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    weekly_summary_text TEXT,
    weekly_image_url    VARCHAR(500),
    weekly_title        VARCHAR(200),
    user_uploaded_image BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_user_year_week UNIQUE (user_id, year, week_number)
);

-- Indexes
CREATE INDEX idx_weekly_diaries_user_id ON weekly_diaries(user_id);
CREATE INDEX idx_weekly_diaries_year_week ON weekly_diaries(year, week_number);
```

**컬럼 설명:**
- `week_id`: 주간 다이어리 고유 ID (PK)
- `user_id`: 작성자 ID (FK)
- `year`, `week_number`: ISO 주차 (UNIQUE 제약)
- `start_date`, `end_date`: 주의 시작/종료일
- `weekly_summary_text`: Gemini AI 생성 주간 요약
- `weekly_title`: Gemini AI 생성 주간 제목
- `weekly_image_url`: 대표 이미지 (AI 생성 or 사용자 업로드)
- `user_uploaded_image`: 사용자가 직접 업로드한 이미지 여부

---

## Backend API 엔드포인트

### Base URL
```
http://172.100.3.26:8000
```

### Authentication (JWT Bearer Token)

#### POST `/api/v1/auth/register`
**사용자 회원가입**

Request:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "홍길동"
}
```

Response (201):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "user_id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "profile_image_url": null,
    "best_streak": 0,
    "current_streak": 0,
    "last_diary_date": null,
    "created_at": "2025-01-13T10:00:00",
    "updated_at": "2025-01-13T10:00:00"
  }
}
```

#### POST `/api/v1/auth/login`
**로그인**

Request:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Response (200): 동일 구조 (JWT 토큰 + 사용자 정보)

#### GET `/api/v1/auth/me`
**현재 사용자 정보 조회**

Headers:
```
Authorization: Bearer {access_token}
```

Response (200):
```json
{
  "user_id": 1,
  "email": "user@example.com",
  "nickname": "홍길동",
  ...
}
```

#### POST `/api/v1/auth/refresh`
**토큰 갱신**

Headers:
```
Authorization: Bearer {access_token}
```

Response:
```json
{
  "access_token": "new_token...",
  "token_type": "bearer",
  "refreshed": true
}
```

---

### Diaries (일일 다이어리)

#### POST `/api/v1/diaries`
**다이어리 생성**

Content-Type: `multipart/form-data`

Form Data:
```
date: "2025-01-13" (YYYY-MM-DD)
description: "오늘 아기가 처음으로 뒤집기를 했어요!"
tag_ids: "[1, 7, 9]" (JSON array string)
photo: (File)
```

Headers:
```
Authorization: Bearer {access_token}
```

**처리 흐름:**
1. 이미지를 `uploads/diaries/` 저장
2. Vision API로 이미지 분석 → `vision_description`
3. Gemini API 호출:
   - `generate_story()` → 동화 생성
   - `analyze_emotion()` → 감정 분석
   - `generate_expert_comment()` → 전문가 의견
4. PostgreSQL에 저장
5. Streak 계산 및 업데이트

Response (201):
```json
{
  "diary_id": 123,
  "user_id": 1,
  "date": "2025-01-13",
  "description": "오늘 아기가 처음으로 뒤집기를 했어요!",
  "photo_url": "diary_1705132800_abc123.jpg",
  "vision_description": "이미지에서 아기, 바닥, 옷, 웃는 얼굴이 감지되었습니다.",
  "generated_story": "어느 화창한 날, 작은 아기가 바닥에 누워있었어요...",
  "expert_comment": "뒤집기는 아기의 대근육 발달의 중요한 이정표입니다...",
  "emotion": "joy",
  "year": 2025,
  "week_number": 3,
  "tags": [
    {"tag_id": 1, "tag_name": "기쁨", "tag_category": "emotion"},
    {"tag_id": 7, "tag_name": "성장", "tag_category": "development"}
  ],
  "created_at": "2025-01-13T10:30:00",
  "updated_at": "2025-01-13T10:30:00"
}
```

#### GET `/api/v1/diaries`
**다이어리 목록 조회 (페이지네이션 + 필터)**

Query Parameters:
```
page: 1 (default)
page_size: 20 (default, max 100)
year: 2025 (optional)
week_number: 3 (optional)
emotion: "joy" (optional)
tag_id: 1 (optional)
```

Response (200):
```json
{
  "diaries": [...],
  "total": 150,
  "page": 1,
  "page_size": 20
}
```

#### GET `/api/v1/diaries/{diary_id}`
**특정 다이어리 조회**

Response (200): 단일 `DiaryResponse` 객체

#### DELETE `/api/v1/diaries/{diary_id}`
**다이어리 삭제**

Response (200):
```json
{
  "message": "Diary deleted successfully"
}
```

**Cascade 삭제:**
- `diary_tags` 자동 삭제 (Foreign Key)
- 이미지 파일 삭제

---

### Weekly Diaries (주간 다이어리)

#### POST `/api/v1/weekly_diaries`
**주간 다이어리 생성**

Content-Type: `multipart/form-data`

Form Data:
```
year: 2025
week_number: 3
photo: (File, optional)
```

Headers:
```
Authorization: Bearer {access_token}
```

**처리 흐름:**
1. 해당 주의 모든 일일 다이어리 조회 (최소 1개 필요)
2. (Optional) 사용자가 업로드한 이미지 저장 → `uploads/weekly/`
3. 모든 일일 다이어리를 결합하여 Gemini API 호출
4. `generate_weekly_summary()` → 제목 + 요약 생성
5. PostgreSQL에 UPSERT (ON CONFLICT UPDATE)

Response (201):
```json
{
  "week_id": 45,
  "user_id": 1,
  "year": 2025,
  "week_number": 3,
  "start_date": "2025-01-13",
  "end_date": "2025-01-19",
  "weekly_summary_text": "이번 주는 아기의 성장이 눈부신 한 주였습니다...",
  "weekly_title": "첫 뒤집기와 미소의 주",
  "weekly_image_url": "weekly_1_2025W03_abc123.jpg",
  "user_uploaded_image": true,
  "created_at": "2025-01-19T20:00:00",
  "updated_at": "2025-01-19T20:00:00"
}
```

#### GET `/api/v1/weekly_diaries`
**주간 다이어리 목록 조회**

Query Parameters:
```
year: 2025 (optional)
```

Response (200):
```json
[
  {
    "week_id": 45,
    "year": 2025,
    "week_number": 3,
    ...
  },
  ...
]
```

#### GET `/api/v1/weekly_diaries/{week_id}`
**주간 다이어리 상세 조회 (일일 다이어리 포함)**

Response (200):
```json
{
  "week_id": 45,
  "year": 2025,
  "week_number": 3,
  "weekly_summary_text": "...",
  "weekly_title": "...",
  "diaries": [
    {
      "diary_id": 123,
      "date": "2025-01-13",
      ...
    },
    ...
  ]
}
```

#### GET `/api/v1/weekly_diaries/by-date/{year}/{week_number}`
**연도/주차로 주간 다이어리 조회**

Example: `/api/v1/weekly_diaries/by-date/2025/3`

Response (200): 동일 구조 (일일 다이어리 포함)

---

### Tags (태그)

#### GET `/api/v1/tags`
**모든 태그 조회 (인증 불필요)**

Response (200):
```json
[
  {"tag_id": 1, "tag_name": "기쁨", "tag_category": "emotion"},
  {"tag_id": 2, "tag_name": "슬픔", "tag_category": "emotion"},
  ...
]
```

#### GET `/api/v1/tags/categories`
**태그 카테고리 목록**

Response (200):
```json
["emotion", "development", "relationship", "health", "activity", "daily"]
```

#### GET `/api/v1/tags/category/{category}`
**카테고리별 태그 조회**

Example: `/api/v1/tags/category/emotion`

Response (200):
```json
[
  {"tag_id": 1, "tag_name": "기쁨", "tag_category": "emotion"},
  {"tag_id": 2, "tag_name": "슬픔", "tag_category": "emotion"},
  ...
]
```

---

## Android 앱 구조

### 패키지 구조
```
com.example.babydiary
├── activity/         # 8개 Activity
├── adapter/          # RecyclerView Adapter
├── dialog/           # 커스텀 Dialog
├── listener/         # Callback Interfaces
├── model/            # Data Models
├── service/          # API Service Layer
└── util/             # Utility Classes
```

---

### Activities (8개)

#### 1. LoginActivity
**파일:** `activity/LoginActivity.java`
**레이아웃:** `res/layout/activity_login.xml`

**기능:**
- 이메일/비밀번호 로그인
- JWT 토큰 저장 (SharedPreferences)
- 회원가입 화면으로 이동
- 자동 로그인 (토큰 유효성 검사)

**주요 코드:**
```java
authService.login(this, email, password, new OnApiResponseListener<TokenResponse>() {
    @Override
    public void onSuccess(TokenResponse response) {
        // 토큰 저장
        SharedPrefsManager.saveToken(context, response.getAccessToken());
        SharedPrefsManager.saveUserId(context, response.getUser().getUserId());

        // MainActivity로 이동
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
});
```

#### 2. RegisterActivity
**파일:** `activity/RegisterActivity.java`

**기능:**
- 회원가입 폼 (이메일, 비밀번호, 닉네임)
- 유효성 검사
- 회원가입 성공 시 자동 로그인

**유효성 검사:**
- 이메일: 정규식 패턴 매칭
- 비밀번호: 최소 6자
- 닉네임: 2~100자

#### 3. MainActivity
**파일:** `activity/MainActivity.java`
**레이아웃:** `res/layout/activity_main.xml`

**기능:**
- 일일 다이어리 목록 표시 (RecyclerView)
- Pull-to-Refresh (SwipeRefreshLayout)
- FloatingActionButton으로 다이어리 작성 화면 이동
- 하단 네비게이션: Profile, Weekly Diary 이동

**RecyclerView Adapter:**
```java
DiaryAdapter adapter = new DiaryAdapter(this, diaries, new OnDiaryClickListener() {
    @Override
    public void onDiaryClick(Diary diary) {
        Intent intent = new Intent(MainActivity.this, DiaryDetailActivity.class);
        intent.putExtra("diary_id", diary.getDiaryId());
        startActivity(intent);
    }

    @Override
    public void onDiaryDelete(Diary diary) {
        showDeleteDialog(diary);
    }
});
```

#### 4. CreateDiaryActivity
**파일:** `activity/CreateDiaryActivity.java`

**기능:**
- 사진 선택 (카메라 or 갤러리)
- 이미지 압축 (ImageUtils)
- 다이어리 내용 입력 (3줄)
- 태그 선택 (가로 스크롤 RecyclerView)
- LoadingDialog 표시
- 완료 후 DiaryDetailActivity로 자동 이동

**이미지 처리:**
```java
// 압축
File photoFile = ImageUtils.compressImage(
    context, imageUri,
    Constants.MAX_IMAGE_WIDTH,  // 1920
    Constants.MAX_IMAGE_HEIGHT, // 1080
    Constants.IMAGE_QUALITY     // 80
);

// Multipart 업로드
Map<String, String> textFields = new HashMap<>();
textFields.put("date", currentDate);
textFields.put("description", description);
textFields.put("tag_ids", gson.toJson(selectedTagIds));

ApiClient.postMultipart(
    Constants.ENDPOINT_DIARIES,
    token,
    photoFile,
    "photo",
    textFields,
    callback
);
```

**LoadingDialog 개선 (Priority 2):**
```java
loadingDialog = LoadingDialog.show(this, "다이어리 생성 중...\n(사진 분석 및 AI 동화 생성)");

diaryService.createDiary(..., new OnApiResponseListener<Diary>() {
    @Override
    public void onSuccess(Diary diary) {
        loadingDialog.dismiss();

        // 생성된 다이어리 상세 화면으로 이동
        Intent intent = new Intent(CreateDiaryActivity.this, DiaryDetailActivity.class);
        intent.putExtra("diary_id", diary.getDiaryId());
        startActivity(intent);
        finish();
    }
});
```

#### 5. DiaryDetailActivity
**파일:** `activity/DiaryDetailActivity.java`

**기능:**
- 다이어리 상세 정보 표시
- 사진 표시 (Glide)
- Vision API 설명 ("AI가 본 사진")
- Gemini 생성 동화 ("AI 그림동화")
- 전문가 의견 ("전문가 조언")
- 감정 및 태그 표시

**UI 구성:**
```
┌─────────────────────┐
│ 날짜: 2025년 1월 13일 │
├─────────────────────┤
│                     │
│   [사진 이미지]       │
│                     │
├─────────────────────┤
│ 오늘의 기록          │
│ 오늘 아기가...       │
├─────────────────────┤
│ AI가 본 사진         │
│ 이미지에서 아기...   │
├─────────────────────┤
│ AI 그림동화          │
│ 어느 화창한 날...    │
├─────────────────────┤
│ 전문가 조언          │
│ 뒤집기는 중요한...   │
├─────────────────────┤
│ 감정: 기쁨           │
│ 태그: 성장, 가족     │
└─────────────────────┘
```

#### 6. WeeklyDiaryListActivity
**파일:** `activity/WeeklyDiaryListActivity.java`

**기능:**
- 주간 다이어리 목록 표시
- FloatingActionButton으로 현재 주 생성
- 이미지 선택 Dialog (Priority 2):
  - "AI가 자동 생성"
  - "이미지 선택하기" → 갤러리

**주간 다이어리 생성 플로우:**
```java
// 1. Dialog 표시
showImageSelectionDialog(year, weekNumber);

// 2-a. AI 자동 생성 선택
createWeeklyDiary(year, weekNumber, null);

// 2-b. 이미지 선택
openGallery();
// → 이미지 압축
// → createWeeklyDiary(year, weekNumber, photoFile);
```

**검증 로직 (3088103 커밋):**
```java
// Step 1: 기존 주간 다이어리 확인
weeklyDiaryService.getWeeklyDiaryByDate(..., new OnApiResponseListener<WeeklyDiary>() {
    @Override
    public void onSuccess(WeeklyDiary existing) {
        // 이미 존재 → 재생성 확인 Dialog
        showRegenerateDialog();
    }

    @Override
    public void onError(String error) {
        if (error.contains("404")) {
            // 없음 → 생성 진행
            createWeeklyDiaryApi(...);
        }
    }
});

// Step 2: 일일 다이어리 개수 검증 (Backend에서 처리)
// 최소 1개 없으면 400 에러 반환
```

#### 7. WeeklyDiaryDetailActivity
**파일:** `activity/WeeklyDiaryDetailActivity.java`

**기능 (Priority 3 개선):**
- 주간 정보 (연도, 주차, 날짜 범위)
- **주간 대표 이미지 표시** (CardView + Glide)
- **AI 생성 주간 제목** 표시
- **AI 생성 주간 요약** 표시
- 해당 주의 모든 일일 다이어리 목록

**구현 코드:**
```java
// 주간 대표 이미지
if (weeklyDiary.getWeeklyImageUrl() != null) {
    cardWeeklyImage.setVisibility(View.VISIBLE);
    String imageUrl = Constants.BASE_URL + "/uploads/weekly/" + weeklyDiary.getWeeklyImageUrl();

    Glide.with(this)
        .load(imageUrl)
        .centerCrop()
        .into(ivWeeklyImage);
}

// AI 주간 제목
if (weeklyDiary.getWeeklyTitle() != null) {
    tvWeeklyTitle.setVisibility(View.VISIBLE);
    tvWeeklyTitle.setText(weeklyDiary.getWeeklyTitle());
}

// AI 주간 요약
if (weeklyDiary.getWeeklySummaryText() != null) {
    tvWeeklySummary.setVisibility(View.VISIBLE);
    tvWeeklySummary.setText(weeklyDiary.getWeeklySummaryText());
}
```

#### 8. ProfileActivity
**파일:** `activity/ProfileActivity.java`

**기능:**
- 사용자 정보 표시 (닉네임, 이메일)
- 연속 기록 통계 (Best Streak, Current Streak)
- 로그아웃 버튼

---

### Service Layer (API 통신)

#### 1. AuthService
**파일:** `service/AuthService.java`

**메서드:**
```java
void register(Context, email, password, nickname, listener)
void login(Context, email, password, listener)
void getCurrentUser(Context, listener)
```

#### 2. DiaryService
**파일:** `service/DiaryService.java`

**메서드:**
```java
void createDiary(Context, description, photoFile, tagIds, listener)
void getDiaries(Context, page, pageSize, listener)
void getDiary(Context, diaryId, listener)
void deleteDiary(Context, diaryId, listener)
```

#### 3. WeeklyDiaryService
**파일:** `service/WeeklyDiaryService.java`

**메서드 (Priority 2 개선):**
```java
// 이미지 없이 생성
void createWeeklyDiary(Context, year, weekNumber, listener)

// 이미지 포함 생성 (overload)
void createWeeklyDiary(Context, year, weekNumber, photoFile, listener)

void getWeeklyDiaries(Context, year, listener)
void getWeeklyDiaryDetail(Context, weekId, listener)
void getWeeklyDiaryByDate(Context, year, weekNumber, listener)
```

#### 4. TagService
**파일:** `service/TagService.java`

**메서드:**
```java
void getAllTags(Context, listener)
void getTagCategories(Context, listener)
void getTagsByCategory(Context, category, listener)
```

---

### Models

#### Diary
**파일:** `model/Diary.java`

```java
public class Diary {
    private int diaryId;
    private int userId;
    private String date;
    private String description;
    private String photoUrl;
    private String visionDescription;
    private String generatedStory;
    private String expertComment;
    private String emotion;
    private int year;
    private int weekNumber;
    private List<Tag> tags;
    private String createdAt;
    private String updatedAt;

    // Getters, Setters
    public String getFullPhotoUrl() {
        return Constants.BASE_URL + "/uploads/" + photoUrl;
    }
}
```

#### WeeklyDiary
**파일:** `model/WeeklyDiary.java`

```java
public class WeeklyDiary {
    private int weekId;
    private int userId;
    private int year;
    private int weekNumber;
    private String startDate;
    private String endDate;
    private String weeklySummaryText;
    private String weeklyImageUrl;
    private String weeklyTitle;
    private boolean userUploadedImage;
    private List<Diary> diaries;
    private String createdAt;
    private String updatedAt;
}
```

#### Tag
```java
public class Tag {
    private int tagId;
    private String tagName;
    private String tagCategory;
}
```

---

### Utilities

#### 1. Constants
**파일:** `util/Constants.java`

**네트워크 설정:**
```java
private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";
private static final String BASE_URL_DEVICE = "http://172.100.3.26:8000";

public static final String BASE_URL = isEmulator() ? BASE_URL_EMULATOR : BASE_URL_DEVICE;

public static boolean isEmulator() {
    return Build.FINGERPRINT.startsWith("generic")
        || Build.MODEL.contains("Emulator")
        || "google_sdk".equals(Build.PRODUCT);
}
```

**API 엔드포인트:**
```java
public static final String API_VERSION = "/api/v1";
public static final String ENDPOINT_REGISTER = API_VERSION + "/auth/register";
public static final String ENDPOINT_LOGIN = API_VERSION + "/auth/login";
public static final String ENDPOINT_DIARIES = API_VERSION + "/diaries";
public static final String ENDPOINT_WEEKLY_DIARIES = API_VERSION + "/weekly_diaries";
```

#### 2. ErrorHandler (Priority 3)
**파일:** `util/ErrorHandler.java`

**기능:**
- HTTP 상태 코드별 에러 처리
- 401: 자동 로그아웃 + 로그인 화면 이동
- 사용자 친화적 에러 메시지

**사용 예시:**
```java
@Override
public void onError(String error) {
    ErrorHandler.handleApiError(error, context);
}
```

**구현:**
```java
public static void handleError(int statusCode, String errorMessage, Context context) {
    switch (statusCode) {
        case 400:
            message = "잘못된 요청입니다.\n" + errorMessage;
            break;
        case 401:
            message = "로그인 정보가 만료되었습니다.\n다시 로그인해주세요.";
            shouldLogout = true;
            break;
        case 404:
            message = "데이터를 찾을 수 없습니다.";
            break;
        case 500:
            message = "서버 오류가 발생했습니다.";
            break;
    }

    if (shouldLogout) {
        SharedPrefsManager.clearToken(context);
        // LoginActivity로 이동
    }
}
```

#### 3. ImageUtils
**파일:** `util/ImageUtils.java`

**기능:**
- 이미지 압축 (해상도 + 품질)
- 임시 파일 생성
- 파일 크기 계산

```java
public static File compressImage(Context context, Uri imageUri,
                                 int maxWidth, int maxHeight, int quality) {
    Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);

    // 비율 유지하면서 리사이즈
    if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
        float ratio = Math.min(
            (float) maxWidth / bitmap.getWidth(),
            (float) maxHeight / bitmap.getHeight()
        );
        int newWidth = Math.round(bitmap.getWidth() * ratio);
        int newHeight = Math.round(bitmap.getHeight() * ratio);
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    // JPEG 압축
    File outputFile = createImageFile(context);
    FileOutputStream out = new FileOutputStream(outputFile);
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

    return outputFile;
}
```

#### 4. SharedPrefsManager
**파일:** `util/SharedPrefsManager.java`

**저장 데이터:**
- `access_token`: JWT 토큰
- `user_id`: 사용자 ID
- `user_email`: 이메일
- `user_nickname`: 닉네임
- `is_logged_in`: 로그인 상태

#### 5. DateUtils
**파일:** `util/DateUtils.java`

**기능:**
- 날짜 포맷 변환 (ISO → 한국어)
- Week number 계산 (ISO 8601)
- Streak 계산

---

### Dialogs

#### LoadingDialog (Priority 2 개선)
**파일:** `dialog/LoadingDialog.java`

**기능:**
- 로딩 중 표시
- **동적 메시지 변경** (setMessage())

**사용 예시:**
```java
LoadingDialog dialog = LoadingDialog.show(context, "다이어리 생성 중...");

// 메시지 변경
dialog.setMessage("AI 동화 생성 중...");

// 닫기
dialog.dismiss();
```

---

## AI 통합

### Google Gemini API

**모델:** `gemini-pro`
**파일:** `backend/app/services/gemini_service.py`

#### 1. 동화 생성 (generate_story)

**프롬프트:**
```python
f"""다음은 아이의 사진 설명과 일기 내용입니다.
이를 바탕으로 5~7세 아이를 위한 짧은 그림동화를 500자 이내로 만들어주세요.
동화는 따뜻하고 긍정적이며, 아이의 경험을 재미있게 표현해야 합니다.

사진 설명: {vision_description}

일기 내용: {diary_description}

그림동화:"""
```

**응답 예시:**
> "어느 화창한 날, 작은 아기가 바닥에 누워있었어요. 갑자기 아기는 용기를 내어 몸을 굴렸어요! 데굴데굴~ 아기가 처음으로 뒤집기에 성공했어요! 엄마와 아빠가 박수를 치며 기뻐했답니다. 아기는 신나서 웃으며 또 한 번 뒤집기를 시도했어요. 앞으로 아기는 더 많은 것을 할 수 있을 거예요!"

#### 2. 감정 분석 (analyze_emotion)

**프롬프트:**
```python
f"""다음 아이 일기의 전반적인 감정을 분석하세요.
반드시 다음 중 하나로만 답변하세요: joy, sadness, anger, surprise, fear, neutral

일기: {diary_description}

감정:"""
```

**응답:** `"joy"` (단일 키워드)

**후처리:**
- 유효한 감정 키워드인지 검증
- 텍스트에서 키워드 추출
- 기본값: `"neutral"`

#### 3. 전문가 의견 (generate_expert_comment)

**프롬프트:**
```python
f"""아이 일기를 읽은 육아전문가로서 부모에게 따뜻한 칭찬과 구체적인 조언을 300자 이내로 작성해주세요.
아이의 감정을 존중하고, 부모의 양육을 격려하며, 실천 가능한 팁을 제공해주세요.

감정: {emotion_kr}

일기: {diary_description}

전문가 의견:"""
```

**응답 예시:**
> "뒤집기는 아기의 대근육 발달의 중요한 이정표입니다. 부모님께서 함께 기뻐해주시는 것이 아이의 자신감과 정서 발달에 큰 도움이 됩니다. 앞으로도 아이의 작은 성취를 함께 축하하며, 안전한 환경에서 자유롭게 탐색할 수 있도록 격려해주세요. 바닥 놀이 시간을 자주 가지면 더 빠른 발달을 기대할 수 있습니다."

#### 4. 주간 요약 (generate_weekly_summary)

**프롬프트:**
```python
f"""다음은 한 주 동안의 아이 일기들입니다.
이 주의 일기들을 읽고, 주간 제목(20자 이내)과 주간 요약(300자 이내)을 작성해주세요.

응답 형식:
제목: [주간 제목]
요약: [주간 요약]

일주일 일기:
{diaries_text}

주간 다이어리:"""
```

**응답 예시:**
```
제목: 첫 뒤집기와 미소의 주
요약: 이번 주는 아기의 성장이 눈부신 한 주였습니다. 월요일에 처음으로 뒤집기에 성공하며 대근육 발달의 큰 이정표를 달성했어요. 수요일에는 엄마 목소리에 반응하며 활짝 웃는 모습을 보여주었고, 금요일에는 장난감을 손으로 잡으려는 시도를 했습니다. 부모님의 따뜻한 격려와 함께, 아기는 매일매일 새로운 것을 배우고 있답니다.
```

**파싱:**
```python
lines = response.split("\n")
for line in lines:
    if line.startswith("제목:"):
        title = line.replace("제목:", "").strip()
    elif line.startswith("요약:"):
        summary = line.replace("요약:", "").strip()
```

---

### Google Vision API

**파일:** `backend/app/services/vision_service.py`

**기능:**
1. 이미지 파일을 Base64 인코딩
2. Vision API 호출 (Label Detection)
3. 감지된 객체들을 한국어 문장으로 생성

**응답 예시:**
```python
{
    "responses": [{
        "labelAnnotations": [
            {"description": "Hand", "score": 0.98},
            {"description": "Finger", "score": 0.95},
            {"description": "Skin", "score": 0.93},
            {"description": "Baby", "score": 0.91}
        ]
    }]
}
```

**생성된 설명:**
> "이미지에서 다음과 같은 요소들이 감지되었습니다: 손, 손가락, 피부, 아기."

---

## 네트워크 설정

### Android → Backend 연결

**에뮬레이터:**
```
10.0.2.2:8000 → 호스트 PC의 localhost:8000
```

**실제 기기 (같은 Wi-Fi):**
```
172.100.3.26:8000 → Mac의 실제 IP
```

**자동 감지 코드:**
```java
public static boolean isEmulator() {
    return Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.MANUFACTURER.contains("Genymotion")
        || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        || "google_sdk".equals(Build.PRODUCT);
}

public static final String BASE_URL = isEmulator()
    ? "http://10.0.2.2:8000"
    : "http://172.100.3.26:8000";
```

### HTTP Cleartext 설정

**파일:** `android_app/app/src/main/res/xml/network_security_config.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">172.100.3.26</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

**AndroidManifest.xml:**
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="true">
```

### API 통신 (ApiClient)

**Multipart 업로드:**
```java
public static void postMultipart(String endpoint, String token, File file,
                                 String fileFieldName, Map<String, String> textFields,
                                 ApiCallback callback) {
    String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();

    HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + endpoint).openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Authorization", "Bearer " + token);
    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

    // 텍스트 필드 추가
    for (Map.Entry<String, String> entry : textFields.entrySet()) {
        dos.writeBytes("--" + boundary + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
        dos.writeBytes(entry.getValue() + "\r\n");
    }

    // 파일 추가
    if (file != null) {
        dos.writeBytes("--" + boundary + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + fileFieldName + "\"; filename=\"" + file.getName() + "\"\r\n");
        dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
        }
        fis.close();
        dos.writeBytes("\r\n");
    }

    dos.writeBytes("--" + boundary + "--\r\n");
    dos.flush();
    dos.close();

    // 응답 처리
    int responseCode = conn.getResponseCode();
    if (responseCode == 200 || responseCode == 201) {
        callback.onSuccess(readResponse(conn.getInputStream()));
    } else {
        callback.onError("HTTP " + responseCode);
    }
}
```

---

## 최근 구현 기능

### Commit 09bb308: IP 주소 업데이트
**문제:** Mac IP 변경 (`172.100.4.137` → `172.100.3.26`)
**해결:** `Constants.java`의 `BASE_URL_DEVICE` 업데이트

---

### Commit c969ae9: Priority 3 권장 기능 완료

#### 1. WeeklyDiaryDetailActivity 이미지 표시
**추가된 UI:**
```xml
<!-- 주간 대표 이미지 -->
<androidx.cardview.widget.CardView
    android:id="@+id/card_weekly_image"
    android:layout_width="match_parent"
    android:layout_height="200dp">
    <ImageView android:id="@+id/iv_weekly_image" />
</androidx.cardview.widget.CardView>

<!-- AI 주간 제목 -->
<TextView android:id="@+id/tv_weekly_title" />

<!-- AI 주간 요약 -->
<TextView android:id="@+id/tv_weekly_summary" />
```

**구현:**
- Glide로 이미지 로딩
- `BASE_URL/uploads/weekly/{filename}`
- 조건부 표시 (데이터 없으면 GONE)

#### 2. ErrorHandler 공통 에러 처리
**파일:** `util/ErrorHandler.java`

**기능:**
- 상태 코드별 메시지
- 401 자동 로그아웃
- AlertDialog 표시

**사용:**
```java
ErrorHandler.handleApiError(error, context);
ErrorHandler.handleError(statusCode, error, context);
```

---

### Commit 487b5f6: Priority 2 기능 개선 완료

#### 1. LoadingDialog 메시지 동적 변경
**추가 메서드:**
```java
public void setMessage(String message) {
    if (tvLoadingMessage != null) {
        tvLoadingMessage.setText(message);
    }
}
```

**사용:**
```java
LoadingDialog dialog = LoadingDialog.show(context, "초기 메시지");
dialog.setMessage("변경된 메시지");
```

#### 2. CreateDiaryActivity 완료 후 상세 화면 이동
**변경 전:**
```java
finish(); // MainActivity로 돌아감
```

**변경 후:**
```java
Intent intent = new Intent(this, DiaryDetailActivity.class);
intent.putExtra("diary_id", diary.getDiaryId());
startActivity(intent);
finish();
```

#### 3. 주간 다이어리 이미지 업로드 선택
**Backend 변경:**
```python
@router.post("", response_model=WeeklyDiaryResponse)
async def create_weekly_diary(
    year: int = Form(...),
    week_number: int = Form(...),
    photo: Optional[UploadFile] = File(None),  # 추가
    ...
):
    if photo:
        # 이미지 저장
        photo_filename = f"weekly_{user_id}_{year}W{week_number:02d}_{uuid.uuid4().hex[:8]}{file_ext}"
        with open(UPLOAD_DIR / photo_filename, "wb") as f:
            f.write(await photo.read())
```

**Android 변경:**
```java
// 1. Dialog 표시
String[] options = {"AI가 자동 생성", "이미지 선택하기"};
new AlertDialog.Builder(this)
    .setTitle("주간 대표 이미지")
    .setItems(options, (dialog, which) -> {
        if (which == 0) {
            createWeeklyDiary(year, week, null);
        } else {
            openGallery();
        }
    })
    .show();

// 2. WeeklyDiaryService overload
public void createWeeklyDiary(Context, year, weekNumber, photoFile, listener) {
    Map<String, String> textFields = new HashMap<>();
    textFields.put("year", String.valueOf(year));
    textFields.put("week_number", String.valueOf(weekNumber));

    ApiClient.postMultipart(ENDPOINT_WEEKLY_DIARIES, token, photoFile, "photo", textFields, ...);
}
```

---

### Commit 69139b4: weekly_diaries 테이블 스키마 수정

**문제:** Backend 코드에서 기대하는 컬럼이 DB에 없음
**에러:** `column "weekly_summary_text" does not exist`

**해결:**
```sql
ALTER TABLE weekly_diaries
ADD COLUMN IF NOT EXISTS weekly_summary_text TEXT,
ADD COLUMN IF NOT EXISTS weekly_image_url VARCHAR(500),
ADD COLUMN IF NOT EXISTS weekly_title VARCHAR(200),
ADD COLUMN IF NOT EXISTS user_uploaded_image BOOLEAN DEFAULT FALSE;
```

**Migration 파일:** `backend/database/migrations/001_add_weekly_diary_columns.sql`

---

### Commit 3088103: 주간 다이어리 생성 로직 개선

**변경 사항:**

1. **2단계 검증:**
   ```java
   // Step 1: 기존 주간 다이어리 확인
   weeklyDiaryService.getWeeklyDiaryByDate(...);

   // Step 2a: 존재하면 → 재생성 확인
   new AlertDialog.Builder(this)
       .setMessage("이미 존재합니다. 다시 생성하시겠습니까?")
       .setPositiveButton("재생성", ...)
       .show();

   // Step 2b: 없으면 → 바로 생성
   createWeeklyDiaryApi(...);
   ```

2. **사용자 친화적 에러 메시지:**
   ```java
   if (error.contains("No diaries found for this week")) {
       message = "이번 주에 작성한 일일 다이어리가 없습니다.\n" +
                "최소 1개 이상의 일일 다이어리가 필요합니다.";
   }
   ```

---

## 전체 프로젝트 통계

### 코드 통계
```
Backend (Python):
- Lines: ~2,500
- Files: 15

Android (Java):
- Lines: ~8,000
- Files: 35
- Activities: 8
- Services: 5
- Models: 4
- Utilities: 8
```

### Git 커밋
```
Total Commits: 10 (최근)
- 09bb308: IP 업데이트
- c969ae9: Priority 3 완료
- 487b5f6: Priority 2 완료
- 69139b4: DB 스키마 수정
- 3088103: 주간 다이어리 로직 개선
- 171e47b: IP 업데이트 (이전)
- 87146db: 핵심 기능 3가지 추가
- 3864634: DiaryDetailActivity 완성
- 888f573: 미구현 기능 추가
- f1dfdf2: 프로젝트 구조 완성
```

### 완성도
```
✅ 데이터베이스: 100% (5개 테이블)
✅ Backend API: 100% (17개 엔드포인트)
✅ Android UI: 100% (8개 Activity)
✅ 네트워크 통신: 100%
✅ AI 통합: 100% (Gemini + Vision)
✅ 이미지 처리: 100%
✅ 인증 시스템: 100%
✅ 에러 처리: 100%

총 완성도: 100% 🎉
```

---

## 개선 가능한 부분 (미구현)

1. **검색 기능**: 다이어리 내용 전체 텍스트 검색
2. **알림 시스템**: 일일 다이어리 작성 리마인더
3. **프로필 이미지 업로드**
4. **다크 모드**
5. **데이터 백업/복원**
6. **소셜 공유 기능**

---

## 실행 방법

### Backend 실행
```bash
cd backend
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# .env 파일 생성
echo "DATABASE_URL=postgresql://kapr@localhost:5432/baby_diary" > .env
echo "SECRET_KEY=your-secret-key" >> .env
echo "GOOGLE_GEMINI_API_KEY=your-api-key" >> .env
echo "GOOGLE_VISION_API_KEY=your-api-key" >> .env

# 서버 실행
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Android 빌드
```bash
cd android_app
./gradlew assembleDebug

# APK 위치
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### PostgreSQL 설정
```bash
psql -d baby_diary -f backend/database/schema.sql
psql -d baby_diary -f backend/database/seed_data.sql
psql -d baby_diary -f backend/database/migrations/001_add_weekly_diary_columns.sql
```

---
