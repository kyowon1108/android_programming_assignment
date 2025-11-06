# Baby Diary Backend API

## 개요
Android 애플리케이션을 위한 REST API 서버 (FastAPI 기반)

## 기술 스택
- **Runtime**: Python 3.9+
- **Framework**: FastAPI 0.109+
- **Database**: PostgreSQL 12+
- **인증**: JWT (JSON Web Tokens)
- **외부 API**:
  - Google Cloud Vision API (이미지 분석)
  - Google Gemini API (AI 텍스트 생성)

## 주요 기능
- 사용자 인증 (JWT)
- 일일 다이어리 CRUD
- 이미지 업로드 및 Vision API 분석
- AI 동화/감정/전문가 의견 생성
- 주간 다이어리 종합 및 요약
- 태그 기반 검색
- 연속 작성 기록 (Streak) 관리

---

## 설치 및 실행

### 사전 요구사항
- Python 3.9 이상
- PostgreSQL 12 이상
- pip (Python 패키지 관리자)

### 1. 가상 환경 생성 및 활성화

#### macOS/Linux
```bash
cd backend
python3 -m venv venv
source venv/bin/activate
```

#### Windows
```bash
cd backend
python -m venv venv
venv\Scripts\activate
```

### 2. 의존성 설치
```bash
pip install -r requirements.txt
```

### 3. 환경 변수 설정
```bash
cp .env.example .env
```

`.env` 파일을 편집하여 필수 값 설정:
```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=baby_diary
DB_USER=postgres
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_super_secret_jwt_key_change_this

# Google API Keys
GOOGLE_VISION_API_KEY=your_vision_api_key
GOOGLE_GEMINI_API_KEY=your_gemini_api_key
```

### 4. 데이터베이스 초기화
데이터베이스를 먼저 생성하고 스키마를 초기화합니다:
```bash
# PostgreSQL 데이터베이스 생성
createdb baby_diary

# 스키마 초기화 (프로젝트 루트의 database 폴더에서)
psql -U postgres -d baby_diary -f ../database/init.sql
```

### 5. 서버 실행

#### 개발 모드 (자동 재시작)
```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

#### 프로덕션 모드
```bash
python main.py
```

서버가 실행되면 다음 주소로 접근 가능:
- API 서버: http://localhost:8000
- API 문서 (Swagger): http://localhost:8000/docs
- API 문서 (ReDoc): http://localhost:8000/redoc

---

## 프로젝트 구조

```
backend/
├── main.py                      # FastAPI 메인 애플리케이션
├── requirements.txt             # Python 의존성
├── .env.example                 # 환경 변수 예시
├── .env                         # 환경 변수 (gitignore)
│
├── app/
│   ├── config/
│   │   ├── settings.py          # 환경 변수 및 설정
│   │   └── database.py          # PostgreSQL 연결 관리
│   │
│   ├── models/
│   │   └── schemas.py           # Pydantic 데이터 검증 모델
│   │
│   ├── routes/
│   │   ├── auth.py              # 인증 엔드포인트
│   │   ├── diaries.py           # 다이어리 CRUD
│   │   ├── weekly_diaries.py    # 주간 다이어리
│   │   └── tags.py              # 태그 관리
│   │
│   ├── services/
│   │   ├── vision_service.py    # Google Vision API 통합
│   │   └── gemini_service.py    # Google Gemini API 통합
│   │
│   └── utils/
│       ├── auth.py              # JWT 토큰 관리
│       ├── file_handler.py      # 파일 업로드 처리
│       └── date_utils.py        # 날짜 유틸리티
│
└── uploads/                     # 업로드된 파일 저장소
    └── diaries/                 # 다이어리 사진
```

---

## API 엔드포인트

### 인증
- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/login` - 로그인
- `GET /api/v1/auth/me` - 현재 사용자 정보
- `POST /api/v1/auth/refresh` - 토큰 갱신

### 다이어리
- `POST /api/v1/diaries` - 다이어리 생성 (+ 사진 업로드)
- `GET /api/v1/diaries` - 다이어리 목록 (페이징, 필터링)
- `GET /api/v1/diaries/{diary_id}` - 다이어리 상세
- `DELETE /api/v1/diaries/{diary_id}` - 다이어리 삭제

### 주간 다이어리
- `POST /api/v1/weekly_diaries` - 주간 다이어리 생성
- `GET /api/v1/weekly_diaries` - 주간 다이어리 목록
- `GET /api/v1/weekly_diaries/{week_id}` - 주간 상세 (+ 일일 다이어리)
- `GET /api/v1/weekly_diaries/by-date/{year}/{week}` - 날짜로 조회

### 태그
- `GET /api/v1/tags` - 모든 태그 목록
- `GET /api/v1/tags/categories` - 태그 카테고리 목록
- `GET /api/v1/tags/category/{category}` - 카테고리별 태그

### 기타
- `GET /` - 루트 (API 정보)
- `GET /health` - 헬스 체크

자세한 API 명세는 [docs/API_SPEC.md](../docs/API_SPEC.md) 참고

---

## 환경 변수 설명

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| `ENVIRONMENT` | 실행 환경 (development/production) | development |
| `HOST` | 서버 호스트 | 0.0.0.0 |
| `PORT` | 서버 포트 | 8000 |
| `DB_HOST` | PostgreSQL 호스트 | localhost |
| `DB_PORT` | PostgreSQL 포트 | 5432 |
| `DB_NAME` | 데이터베이스 이름 | baby_diary |
| `DB_USER` | 데이터베이스 사용자 | postgres |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | - |
| `JWT_SECRET` | JWT 토큰 시크릿 키 | - |
| `JWT_ALGORITHM` | JWT 알고리즘 | HS256 |
| `JWT_EXPIRATION_HOURS` | JWT 토큰 유효 시간 (시간) | 720 (30일) |
| `GOOGLE_VISION_API_KEY` | Google Vision API 키 | - |
| `GOOGLE_GEMINI_API_KEY` | Google Gemini API 키 | - |
| `UPLOAD_DIR` | 업로드 파일 저장 경로 | ./uploads |
| `MAX_FILE_SIZE` | 최대 파일 크기 (바이트) | 10485760 (10MB) |
| `CORS_ORIGINS` | CORS 허용 오리진 (쉼표 구분) | http://localhost:8080 |
| `LOG_LEVEL` | 로그 레벨 | INFO |

---

## Google API 키 설정

### Google Cloud Vision API
1. [Google Cloud Console](https://console.cloud.google.com) 접속
2. 새 프로젝트 생성 또는 기존 프로젝트 선택
3. "API 및 서비스" > "라이브러리" 이동
4. "Cloud Vision API" 검색 및 활성화
5. "API 및 서비스" > "사용자 인증 정보" 이동
6. "사용자 인증 정보 만들기" > "API 키" 선택
7. 생성된 API 키를 `.env` 파일의 `GOOGLE_VISION_API_KEY`에 설정

### Google Gemini API
1. [Google AI Studio](https://makersuite.google.com/app/apikey) 접속
2. "Create API Key" 클릭
3. 생성된 API 키를 `.env` 파일의 `GOOGLE_GEMINI_API_KEY`에 설정

---

## 개발 가이드

### 코드 스타일
- PEP 8 준수
- Type hints 사용
- Docstrings 작성

### 새 엔드포인트 추가
1. `app/routes/`에 라우터 파일 생성
2. `main.py`에서 라우터 등록:
```python
from app.routes import new_router
app.include_router(new_router.router)
```

### 데이터베이스 마이그레이션
현재는 수동 SQL 스크립트 사용. 향후 Alembic 도입 고려.

---

## 테스트

### 단위 테스트 (예정)
```bash
pytest
```

### API 테스트
Swagger UI를 통한 수동 테스트:
http://localhost:8000/docs

또는 `curl`/`httpie` 사용:
```bash
# 회원가입
curl -X POST http://localhost:8000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123","nickname":"테스터"}'

# 로그인
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

---

## 문제 해결

### 데이터베이스 연결 오류
```bash
# PostgreSQL 서비스 상태 확인
sudo service postgresql status

# 서비스 재시작
sudo service postgresql restart

# 연결 테스트
psql -U postgres -d baby_diary
```

### 포트 충돌
`.env` 파일에서 `PORT` 값을 변경하거나 실행 시 지정:
```bash
uvicorn main:app --port 8001
```

### Google API 에러
- API 키가 올바른지 확인
- Google Cloud Console에서 API가 활성화되었는지 확인
- API 할당량 초과 여부 확인

---

## 배포

### 로컬 서비스화 (systemd)
1. `/etc/systemd/system/baby-diary-api.service` 생성:
```ini
[Unit]
Description=Baby Diary API
After=network.target postgresql.service

[Service]
Type=simple
User=your_username
WorkingDirectory=/path/to/backend
Environment="PATH=/path/to/backend/venv/bin"
ExecStart=/path/to/backend/venv/bin/uvicorn main:app --host 0.0.0.0 --port 8000
Restart=always

[Install]
WantedBy=multi-user.target
```

2. 서비스 활성화 및 시작:
```bash
sudo systemctl enable baby-diary-api
sudo systemctl start baby-diary-api
sudo systemctl status baby-diary-api
```

---

## 보안 고려사항
- `.env` 파일은 절대 Git에 커밋하지 않음
- JWT 시크릿 키는 강력하게 설정 (최소 32자)
- 프로덕션 환경에서는 HTTPS 사용
- API 키는 환경 변수로만 관리
- 파일 업로드 시 검증 강화

---

## 라이선스
MIT

---

## 개발자
kyowon1108

## 문의
이메일: kyowon1108@gmail.com
