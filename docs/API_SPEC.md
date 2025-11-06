# Baby Diary API 명세서

## 개요
Baby Diary Android 애플리케이션을 위한 REST API 명세

**Base URL**: `http://localhost:8000`
**API Version**: v1
**Authentication**: JWT Bearer Token

## 목차
1. [인증 (Authentication)](#인증-authentication)
2. [다이어리 (Diaries)](#다이어리-diaries)
3. [주간 다이어리 (Weekly Diaries)](#주간-다이어리-weekly-diaries)
4. [태그 (Tags)](#태그-tags)
5. [에러 응답](#에러-응답)

---

## 인증 (Authentication)

### 회원가입
사용자 계정 생성

**Endpoint**: `POST /api/v1/auth/register`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "홍길동"
}
```

**Response** (201 Created):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "user": {
    "user_id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "profile_image_url": null,
    "best_streak": 0,
    "current_streak": 0,
    "last_diary_date": null,
    "created_at": "2025-01-06T10:00:00",
    "updated_at": "2025-01-06T10:00:00"
  }
}
```

**Errors**:
- `400 Bad Request`: 이메일이 이미 등록됨
- `422 Unprocessable Entity`: 잘못된 입력 데이터

---

### 로그인
사용자 로그인 및 JWT 토큰 발급

**Endpoint**: `POST /api/v1/auth/login`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "user": {
    "user_id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "profile_image_url": null,
    "best_streak": 5,
    "current_streak": 3,
    "last_diary_date": "2025-01-05",
    "created_at": "2025-01-01T10:00:00",
    "updated_at": "2025-01-06T10:00:00"
  }
}
```

**Errors**:
- `401 Unauthorized`: 잘못된 이메일 또는 비밀번호

---

### 현재 사용자 정보
로그인한 사용자 정보 조회

**Endpoint**: `GET /api/v1/auth/me`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (200 OK):
```json
{
  "user_id": 1,
  "email": "user@example.com",
  "nickname": "홍길동",
  "profile_image_url": null,
  "best_streak": 5,
  "current_streak": 3,
  "last_diary_date": "2025-01-05",
  "created_at": "2025-01-01T10:00:00",
  "updated_at": "2025-01-06T10:00:00"
}
```

**Errors**:
- `401 Unauthorized`: 유효하지 않은 토큰
- `404 Not Found`: 사용자를 찾을 수 없음

---

### 토큰 갱신
JWT 토큰 자동 갱신 (만료 7일 전부터)

**Endpoint**: `POST /api/v1/auth/refresh`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (200 OK):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "refreshed": true
}
```

또는 (갱신 불필요 시):
```json
{
  "message": "Token is still valid",
  "refreshed": false
}
```

---

## 다이어리 (Diaries)

### 다이어리 생성
새로운 일일 다이어리 작성

**Endpoint**: `POST /api/v1/diaries`

**Headers**:
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

**Form Data**:
- `date` (string, required): 날짜 (YYYY-MM-DD)
- `description` (string, required): 일기 내용 (3줄 텍스트)
- `tag_ids` (string, optional): 태그 ID 배열 JSON (예: "[1,2,3]")
- `photo` (file, required): 이미지 파일 (JPG, PNG)

**Response** (201 Created):
```json
{
  "diary_id": 1,
  "user_id": 1,
  "date": "2025-01-06",
  "description": "오늘은 공원에서 친구들과 즐겁게 놀았어요...",
  "photo_url": "diaries/abc123.jpg",
  "vision_description": "이미지에서 다음과 같은 요소들이 감지되었습니다: 놀이터, 어린이, 미끄럼틀.",
  "generated_story": "어느 화창한 날, 공원에 작은 친구가 놀러 왔어요...",
  "expert_comment": "아이가 친구들과 즐겁게 노는 모습이 보기 좋네요...",
  "emotion": "joy",
  "year": 2025,
  "week_number": 1,
  "tags": [
    {
      "tag_id": 1,
      "tag_name": "기쁨",
      "tag_category": "emotion"
    }
  ],
  "created_at": "2025-01-06T14:30:00",
  "updated_at": "2025-01-06T14:30:00"
}
```

**Errors**:
- `400 Bad Request`: 해당 날짜에 이미 다이어리 존재
- `401 Unauthorized`: 인증 실패
- `422 Unprocessable Entity`: 잘못된 입력 데이터

---

### 다이어리 목록 조회
사용자의 다이어리 목록 (페이징, 필터링)

**Endpoint**: `GET /api/v1/diaries`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Query Parameters**:
- `page` (integer, optional): 페이지 번호 (기본: 1)
- `page_size` (integer, optional): 페이지당 항목 수 (기본: 20, 최대: 100)
- `year` (integer, optional): 연도 필터
- `week_number` (integer, optional): 주차 필터
- `emotion` (string, optional): 감정 필터 (joy, sadness, anger, surprise, fear, neutral)
- `tag_id` (integer, optional): 태그 ID 필터

**Example**: `GET /api/v1/diaries?page=1&page_size=10&year=2025&emotion=joy`

**Response** (200 OK):
```json
{
  "diaries": [
    {
      "diary_id": 1,
      "user_id": 1,
      "date": "2025-01-06",
      "description": "...",
      "photo_url": "diaries/abc123.jpg",
      "vision_description": "...",
      "generated_story": "...",
      "expert_comment": "...",
      "emotion": "joy",
      "year": 2025,
      "week_number": 1,
      "tags": [...],
      "created_at": "2025-01-06T14:30:00",
      "updated_at": "2025-01-06T14:30:00"
    }
  ],
  "total": 25,
  "page": 1,
  "page_size": 10
}
```

---

### 다이어리 상세 조회
특정 다이어리 상세 정보

**Endpoint**: `GET /api/v1/diaries/{diary_id}`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (200 OK):
```json
{
  "diary_id": 1,
  "user_id": 1,
  "date": "2025-01-06",
  "description": "...",
  "photo_url": "diaries/abc123.jpg",
  "vision_description": "...",
  "generated_story": "...",
  "expert_comment": "...",
  "emotion": "joy",
  "year": 2025,
  "week_number": 1,
  "tags": [...],
  "created_at": "2025-01-06T14:30:00",
  "updated_at": "2025-01-06T14:30:00"
}
```

**Errors**:
- `404 Not Found`: 다이어리를 찾을 수 없음

---

### 다이어리 삭제
다이어리 삭제

**Endpoint**: `DELETE /api/v1/diaries/{diary_id}`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (200 OK):
```json
{
  "message": "Diary deleted successfully"
}
```

**Errors**:
- `404 Not Found`: 다이어리를 찾을 수 없음

---

## 주간 다이어리 (Weekly Diaries)

### 주간 다이어리 생성
주간 다이어리 생성 또는 재생성

**Endpoint**: `POST /api/v1/weekly_diaries`

**Headers**:
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "year": 2025,
  "week_number": 1
}
```

**Response** (201 Created):
```json
{
  "week_id": 1,
  "user_id": 1,
  "year": 2025,
  "week_number": 1,
  "start_date": "2024-12-30",
  "end_date": "2025-01-05",
  "weekly_summary_text": "이번 주는 아이가 새로운 친구들을 많이 사귀었고...",
  "weekly_image_url": null,
  "weekly_title": "새로운 친구들과의 한 주",
  "user_uploaded_image": false,
  "created_at": "2025-01-06T15:00:00",
  "updated_at": "2025-01-06T15:00:00"
}
```

**Errors**:
- `400 Bad Request`: 해당 주에 다이어리가 없음

---

### 주간 다이어리 목록
사용자의 주간 다이어리 목록

**Endpoint**: `GET /api/v1/weekly_diaries`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Query Parameters**:
- `year` (integer, optional): 연도 필터

**Response** (200 OK):
```json
[
  {
    "week_id": 1,
    "user_id": 1,
    "year": 2025,
    "week_number": 1,
    "start_date": "2024-12-30",
    "end_date": "2025-01-05",
    "weekly_summary_text": "...",
    "weekly_image_url": null,
    "weekly_title": "새로운 친구들과의 한 주",
    "user_uploaded_image": false,
    "created_at": "2025-01-06T15:00:00",
    "updated_at": "2025-01-06T15:00:00"
  }
]
```

---

### 주간 다이어리 상세 (전체)
주간 다이어리 + 포함된 일일 다이어리들

**Endpoint**: `GET /api/v1/weekly_diaries/{week_id}`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (200 OK):
```json
{
  "week_id": 1,
  "user_id": 1,
  "year": 2025,
  "week_number": 1,
  "start_date": "2024-12-30",
  "end_date": "2025-01-05",
  "weekly_summary_text": "...",
  "weekly_image_url": null,
  "weekly_title": "새로운 친구들과의 한 주",
  "user_uploaded_image": false,
  "diaries": [
    {
      "diary_id": 1,
      "date": "2025-01-01",
      ...
    },
    {
      "diary_id": 2,
      "date": "2025-01-02",
      ...
    }
  ],
  "created_at": "2025-01-06T15:00:00",
  "updated_at": "2025-01-06T15:00:00"
}
```

---

### 주간 다이어리 날짜로 조회
연도와 주차로 주간 다이어리 조회

**Endpoint**: `GET /api/v1/weekly_diaries/by-date/{year}/{week_number}`

**Headers**:
```
Authorization: Bearer <access_token>
```

**Example**: `GET /api/v1/weekly_diaries/by-date/2025/1`

**Response** (200 OK): 주간 다이어리 상세와 동일

**Errors**:
- `404 Not Found`: 주간 다이어리를 찾을 수 없음

---

## 태그 (Tags)

### 모든 태그 조회
사용 가능한 모든 태그 목록

**Endpoint**: `GET /api/v1/tags`

**Response** (200 OK):
```json
[
  {
    "tag_id": 1,
    "tag_name": "기쁨",
    "tag_category": "emotion"
  },
  {
    "tag_id": 2,
    "tag_name": "슬픔",
    "tag_category": "emotion"
  },
  {
    "tag_id": 7,
    "tag_name": "성장",
    "tag_category": "development"
  }
]
```

---

### 태그 카테고리 목록
태그 카테고리 목록

**Endpoint**: `GET /api/v1/tags/categories`

**Response** (200 OK):
```json
[
  "activity",
  "development",
  "emotion",
  "health",
  "memory",
  "relationship"
]
```

---

### 카테고리별 태그 조회
특정 카테고리의 태그들

**Endpoint**: `GET /api/v1/tags/category/{category}`

**Example**: `GET /api/v1/tags/category/emotion`

**Response** (200 OK):
```json
[
  {
    "tag_id": 1,
    "tag_name": "기쁨",
    "tag_category": "emotion"
  },
  {
    "tag_id": 2,
    "tag_name": "슬픔",
    "tag_category": "emotion"
  }
]
```

---

## 에러 응답

### 공통 에러 형식
```json
{
  "error": "Error message",
  "detail": "Detailed error description"
}
```

### HTTP 상태 코드
- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `404 Not Found`: 리소스를 찾을 수 없음
- `422 Unprocessable Entity`: 데이터 검증 실패
- `500 Internal Server Error`: 서버 오류

---

## 인증 방식

### JWT Bearer Token
모든 보호된 엔드포인트는 `Authorization` 헤더에 JWT 토큰이 필요합니다:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 토큰 만료
- 토큰 유효 기간: 30일 (720시간)
- 만료 7일 전부터 `/api/v1/auth/refresh` 엔드포인트로 자동 갱신 가능

---

## 감정 (Emotion) 값
- `joy`: 기쁨
- `sadness`: 슬픔
- `anger`: 화남
- `surprise`: 놀람
- `fear`: 두려움
- `neutral`: 평온

---

## 파일 업로드

### 이미지 파일
- 지원 형식: JPG, JPEG, PNG, GIF, BMP
- 최대 크기: 10MB
- 업로드 후 저장 경로: `/uploads/diaries/{filename}`

### 정적 파일 접근
업로드된 이미지는 다음 경로로 접근 가능:
```
http://localhost:8000/uploads/diaries/abc123.jpg
```

---

## API 문서 자동 생성
FastAPI는 자동 API 문서를 제공합니다:

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
