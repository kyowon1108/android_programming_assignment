# System Architecture

## Overview

Baby Diary는 3-Tier 아키텍처를 기반으로 한 모바일 애플리케이션입니다.

```
┌──────────────────────────────────────────────────────┐
│                   Client Tier                        │
│                 (Android App)                        │
├──────────────────────────────────────────────────────┤
│                Application Tier                      │
│              (FastAPI Backend)                       │
├──────────────────────────────────────────────────────┤
│                   Data Tier                          │
│                 (PostgreSQL)                         │
└──────────────────────────────────────────────────────┘
```

## Component Architecture

```
                     ┌─────────────────┐
                     │  Android App    │
                     │   (Java)        │
                     └────────┬────────┘
                              │
                              │ HTTPS/REST
                              │
                     ┌────────▼────────┐
                     │  FastAPI Server │
                     │   (Python)      │
                     └────┬────────┬───┘
                          │        │
                    ┌─────▼──┐  ┌─▼──────────┐
                    │  APIs   │  │ PostgreSQL │
                    ├─────────┤  └────────────┘
                    │ Vision  │
                    │ Gemini  │
                    └─────────┘
```

## 1. Client Tier (Android Application)

### Technology Stack
- **Language**: Java
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34

### Architecture Pattern: MVP (Model-View-Presenter)

```
View (Activity) ←→ Presenter (Service) ←→ Model (Data Classes)
```

### Key Components

#### Activities (View)
- `LoginActivity`: 로그인 화면
- `RegisterActivity`: 회원가입 화면
- `MainActivity`: 메인 다이어리 목록
- `CreateDiaryActivity`: 다이어리 작성
- `DiaryDetailActivity`: 다이어리 상세
- `ProfileActivity`: 프로필 관리
- `WeeklyDiaryListActivity`: 주간 다이어리 목록
- `WeeklyDiaryDetailActivity`: 주간 다이어리 상세

#### Services (Business Logic)
- `AuthService`: 인증 관련 비즈니스 로직
- `DiaryService`: 다이어리 CRUD
- `UserService`: 사용자 정보 관리
- `ApiClient`: HTTP 통신 기반 클래스
- `WeeklyDiaryService`: 주간 다이어리 처리

#### Models (Data)
- `User`: 사용자 정보
- `Diary`: 다이어리 정보
- `WeeklyDiary`: 주간 다이어리
- `Tag`: 태그 정보
- `LoginRequest/Response`: 로그인 데이터

#### Database (Local Cache)
- `DatabaseHelper`: SQLite 관리
- `DiaryDbHelper`: 다이어리 로컬 캐시
- `UserDbHelper`: 사용자 정보 캐시
- `TagDbHelper`: 태그 캐시

#### Utilities
- `Constants`: 상수 정의
- `DateUtils`: 날짜 처리
- `ImageUtils`: 이미지 처리
- `NetworkUtils`: 네트워크 상태
- `PermissionUtils`: 권한 관리
- `SharedPrefsManager`: SharedPreferences 관리
- `ValidationUtils`: 입력값 검증

### Data Flow
```
User Input → Activity → Service → ApiClient → Backend API
                ↓                      ↓
            Database              SharedPrefs
            (Cache)               (Token/Settings)
```

## 2. Application Tier (Backend Server)

### Technology Stack
- **Framework**: FastAPI (Python)
- **ASGI Server**: Uvicorn
- **Authentication**: JWT

### Architecture Pattern: Layered Architecture

```
Routes → Controllers → Services → Database
                    ↓
               External APIs
```

### Key Components

#### Routes (API Endpoints)
- `/api/v1/auth/*`: 인증 관련
- `/api/v1/diaries/*`: 다이어리 CRUD
- `/api/v1/tags/*`: 태그 관리
- `/api/v1/weekly_diaries/*`: 주간 다이어리

#### Services
- `vision_service`: Google Vision API 연동
- `gemini_service`: Google Gemini API 연동
- 이미지 분석, 동화 생성, 감정 분석, 전문가 의견 생성

#### Middleware
- Authentication middleware (JWT 검증)
- Error handling middleware
- CORS middleware

#### Configuration
- Environment variables (`.env`)
- Database connection pooling
- API rate limiting

### API Communication Flow
```
Request → Middleware → Route → Service → External API
            ↓                     ↓
       Auth Check            Database
```

## 3. Data Tier (Database)

### Technology
- **Database**: PostgreSQL 12+
- **Connection**: psycopg2 (Python)

### Schema Design
- Normalized to 3NF
- Foreign key constraints
- Indexes for performance

### Key Tables
1. `users`: 사용자 정보
2. `diaries`: 다이어리 데이터
3. `tags`: 태그 마스터
4. `diary_tags`: M:N 관계
5. `weekly_diaries`: 주간 요약

## 4. External Services Integration

### Google Vision API
```
Image → Base64 → Vision API → Labels/Text → Description
```
- 이미지 라벨 감지
- 텍스트 추출
- Safe search detection

### Google Gemini API
```
Description + Diary → Gemini → Story/Emotion/Comment
```
- 동화 생성 (5-7세 대상)
- 감정 분석 (6가지 감정)
- 전문가 의견 생성

## 5. Security Architecture

### Authentication & Authorization
```
Login → JWT Token → Authorization Header → Backend Validation
```

### Security Measures
1. **Password Security**: bcrypt hashing
2. **Token Management**: JWT with expiration
3. **API Security**: Rate limiting, CORS
4. **Data Protection**: HTTPS only
5. **Input Validation**: Client + Server side
6. **SQL Injection Prevention**: Parameterized queries

### Network Security
- Certificate pinning (planned)
- Network security config (cleartext disabled in production)

## 6. Performance Optimization

### Client-Side
1. **Image Optimization**
   - Compression before upload
   - Thumbnail generation
   - Cache management

2. **Network Optimization**
   - Connection pooling
   - Request batching
   - Offline mode with SQLite

3. **UI Performance**
   - RecyclerView for lists
   - Async image loading
   - Background thread for network

### Server-Side
1. **Database Optimization**
   - Connection pooling
   - Query optimization
   - Proper indexing

2. **Caching Strategy**
   - Redis for session (planned)
   - CDN for images (planned)

3. **API Optimization**
   - Pagination
   - Field filtering
   - Response compression

## 7. Deployment Architecture

### Development
```
Local Android Studio ←→ Local Backend ←→ Local PostgreSQL
```

### Production (Planned)
```
Google Play Store → Android App
        ↓
    AWS/GCP
        ↓
   Load Balancer
        ↓
   FastAPI Cluster
        ↓
   RDS PostgreSQL
```

## 8. Monitoring & Logging

### Client-Side
- Crash reporting (Crashlytics - planned)
- Analytics (Google Analytics - planned)
- Performance monitoring

### Server-Side
- Application logs (Winston/Python logging)
- Access logs
- Error tracking
- Performance metrics

## 9. Scalability Considerations

### Horizontal Scaling
- Stateless backend design
- Database read replicas
- Load balancing

### Vertical Scaling
- Resource monitoring
- Auto-scaling policies
- Database optimization

## 10. Future Enhancements

### Technical Debt
1. Implement caching layer (Redis)
2. Add message queue for async tasks
3. Implement CI/CD pipeline
4. Add comprehensive testing

### Feature Enhancements
1. Real-time notifications
2. Multi-language support
3. Dark mode
4. Offline sync
5. Social sharing

## 11. Development Workflow

```
Feature Branch → Development → Testing → Staging → Production
        ↓            ↓           ↓         ↓          ↓
    Git Branch    Local Env   Test Env  Stage Env  Prod Env
```

## 12. Technology Decisions

### Why FastAPI?
- Modern Python framework
- Automatic API documentation
- Type hints support
- High performance

### Why PostgreSQL?
- ACID compliance
- JSON support
- Full-text search
- Proven reliability

### Why JWT?
- Stateless authentication
- Mobile-friendly
- Standard protocol
- Secure

## Conclusion

이 아키텍처는 확장 가능하고, 유지보수가 용이하며, 보안이 강화된 시스템을 구축하는 것을 목표로 설계되었습니다.