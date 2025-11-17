# Baby Diary Android Application

육아 일기를 작성하고 관리하는 Android 애플리케이션입니다.

## 프로젝트 개요

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **언어**: Java
- **빌드 시스템**: Gradle (Kotlin DSL)

## 주요 기능

1. **사용자 인증**
   - 회원가입
   - 로그인/로그아웃
   - 토큰 기반 인증

2. **일기 관리**
   - 일기 작성 (텍스트 및 이미지 첨부)
   - 일기 목록 조회
   - 일기 상세 보기
   - 일기 수정 및 삭제

3. **주간 요약**
   - 주별 일기 요약 조회
   - 주간 다이어리 상세 보기

4. **검색**
   - 일기 내용 검색
   - 날짜별 검색

5. **프로필**
   - 사용자 정보 조회
   - 닉네임 수정

## 프로젝트 구조

```
app/src/main/java/com/example/babydiary/
├── BabyDiaryApplication.java      # Application 클래스
├── data/                           # 데이터 계층
│   ├── api/                        # API 인터페이스
│   │   ├── AuthApi.java            # 인증 관련 API
│   │   ├── DiaryApi.java           # 일기 관련 API
│   │   ├── TagApi.java             # 태그 관련 API
│   │   ├── UserApi.java            # 사용자 정보 API
│   │   └── WeeklyDiaryApi.java     # 주간 다이어리 API
│   ├── dto/                        # 데이터 전송 객체
│   │   ├── DiaryListResponse.java
│   │   ├── DiaryResponse.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── MessageResponse.java
│   │   ├── RegisterRequest.java
│   │   ├── Tag.java
│   │   ├── UpdateNicknameRequest.java
│   │   ├── UserProfileResponse.java
│   │   ├── WeeklyDiaryResponse.java
│   │   └── WeeklyDiaryWithDiariesResponse.java
│   └── network/                    # 네트워크 설정
│       └── ApiClient.java          # Retrofit 클라이언트
├── ui/                             # UI 계층
│   ├── auth/                       # 인증 화면
│   │   ├── LoginActivity.java      # 로그인 화면
│   │   └── RegisterActivity.java   # 회원가입 화면
│   ├── diary/                      # 일기 관련 화면
│   │   ├── CreateDiaryActivity.java    # 일기 작성
│   │   ├── DiaryAdapter.java           # 일기 목록 어댑터
│   │   ├── DiaryDetailActivity.java    # 일기 상세
│   │   └── DiaryListFragment.java      # 일기 목록
│   ├── main/
│   │   └── MainActivity.java       # 메인 액티비티
│   ├── profile/
│   │   └── ProfileFragment.java    # 프로필 화면
│   ├── search/
│   │   └── SearchFragment.java     # 검색 화면
│   └── weekly/                     # 주간 다이어리
│       ├── WeeklyDiaryAdapter.java         # 주간 다이어리 어댑터
│       ├── WeeklyDiaryDetailActivity.java  # 주간 상세
│       └── WeeklyDiaryListFragment.java    # 주간 목록
└── utils/                          # 유틸리티
    ├── AuthUtils.java              # 인증 유틸
    ├── DateUtils.java              # 날짜 유틸
    └── FileUtils.java              # 파일 유틸
```

## 화면 구성

### 1. 인증 화면
- **LoginActivity**: 이메일과 비밀번호를 통한 로그인
- **RegisterActivity**: 신규 사용자 회원가입

### 2. 메인 화면 (MainActivity)
하단 네비게이션 바를 통해 4개의 주요 화면 전환:

#### 2.1 일기 목록 (DiaryListFragment)
- 작성된 일기 목록을 RecyclerView로 표시
- Floating Action Button(FAB)을 통한 새 일기 작성
- 각 아이템 클릭 시 상세 화면으로 이동

#### 2.2 주간 요약 (WeeklyDiaryListFragment)
- 주별로 그룹화된 일기 요약 표시
- 주간 단위로 일기 내용을 한눈에 확인

#### 2.3 검색 (SearchFragment)
- 키워드를 통한 일기 내용 검색
- 날짜별 필터링 기능

#### 2.4 프로필 (ProfileFragment)
- 사용자 정보 표시
- 닉네임 수정
- 로그아웃 기능

### 3. 상세 화면
- **DiaryDetailActivity**: 개별 일기 상세 보기, 수정, 삭제
- **CreateDiaryActivity**: 새 일기 작성, 이미지 첨부
- **WeeklyDiaryDetailActivity**: 주간 다이어리 상세 보기

## 네트워크 통신

Retrofit2와 OkHttp를 사용하여 RESTful API와 통신:
- 기본 URL: http://43.201.221.184:8080/api/
- JWT 토큰 기반 인증
- JSON 데이터 포맷 사용

## 의존성 라이브러리

- **AndroidX Core Libraries**: 기본 Android 컴포넌트
- **Material Design Components**: UI 컴포넌트
- **Retrofit2 & OkHttp**: 네트워크 통신
- **Gson**: JSON 파싱
- **Glide**: 이미지 로딩
- **SwipeRefreshLayout**: 당겨서 새로고침
- **ViewBinding**: 뷰 바인딩

## 빌드 및 실행

1. Android Studio에서 프로젝트 열기
2. Gradle 동기화 실행
3. 에뮬레이터 또는 실제 기기에서 실행

## 요구사항

- Android Studio Arctic Fox 이상
- JDK 8 이상
- Android SDK 34