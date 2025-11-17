# Baby Diary Android Application

육아 일기를 작성하고 관리하는 Android 애플리케이션입니다. AI 기반 이미지 분석, 감정 분석, 주간 요약 생성 기능을 제공합니다.

## 프로젝트 개요

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **언어**: Java
- **빌드 시스템**: Gradle (Kotlin DSL)
- **Version**: 1.0

## 주요 기능

### 1. 사용자 인증
- 이메일/비밀번호 기반 회원가입
- 로그인/로그아웃
- JWT 토큰 기반 인증
- 자동 로그인 (SharedPreferences를 통한 토큰 저장)

### 2. 일기 관리
- **작성 기능**
  - 날짜 선택 (DatePicker)
  - 텍스트 입력 (멀티라인)
  - 갤러리에서 사진 선택 및 첨부
  - 태그 선택 (다중 선택 가능, ChipGroup)
  - 권한 관리 (READ_MEDIA_IMAGES, READ_EXTERNAL_STORAGE)
- **조회 기능**
  - 일기 목록 조회 (RecyclerView, 카드 레이아웃)
  - 일기 상세 보기
  - 이미지 확대 보기 (Glide)
  - 당겨서 새로고침 (SwipeRefreshLayout)
  - 무한 스크롤 페이지네이션
- **AI 분석 기능**
  - 이미지 비전 분석 (vision_description)
  - AI 생성 스토리 (generated_story)
  - 전문가 코멘트 (expert_comment)
  - 감정 분석 (joy, sadness, anger, surprise, fear, neutral)
- **삭제 기능**
  - 일기 삭제 (확인 다이얼로그)

### 3. 주간 요약
- 주별 일기 자동 그룹화 (year, week_number)
- AI 기반 주간 요약 텍스트 생성
- 주간 대표 이미지 (AI 생성 또는 사용자 업로드)
- 주간 타이틀 자동 생성
- 주간 다이어리 상세 보기 (해당 주의 모든 일기 포함)

### 4. 검색 기능
- **다양한 필터 옵션**
  - 키워드 검색 (일기 내용)
  - 날짜 범위 필터 (시작일/종료일 선택)
  - 감정 필터 (6가지 감정 카테고리)
  - 태그 필터 (사용자 정의 태그)
- **페이지네이션**
  - 무한 스크롤
  - 20개씩 로드 (offset/limit)
- **검색 결과**
  - RecyclerView로 표시
  - 일기 카드 클릭 시 상세 화면 이동

### 5. 프로필 관리
- 사용자 정보 조회 (이메일, 닉네임)
- 닉네임 실시간 수정
- 당겨서 새로고침 (SwipeRefreshLayout)
- 로그아웃 (확인 다이얼로그)

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

#### LoginActivity
- **UI 구성**
  - 이메일 입력 필드
  - 비밀번호 입력 필드
  - 로그인 버튼
  - 회원가입 화면 이동 버튼
- **기능**
  - 입력 유효성 검사
  - JWT 토큰 수신 및 저장
  - 자동 로그인 (토큰 확인)
  - 로그인 성공 시 MainActivity로 이동

#### RegisterActivity
- **UI 구성**
  - 이메일 입력 필드
  - 비밀번호 입력 필드
  - 비밀번호 확인 필드
  - 닉네임 입력 필드
  - 회원가입 버튼
- **기능**
  - 입력 유효성 검사 (이메일 형식, 비밀번호 일치)
  - 회원가입 API 호출
  - 성공 시 LoginActivity로 이동

### 2. 메인 화면 (MainActivity)

앱의 중심 화면으로 하단 네비게이션 바(BottomNavigationView)를 통해 4개의 Fragment 전환

#### 2.1 홈 - 일기 목록 (DiaryListFragment)
- **UI 구성**
  - RecyclerView (LinearLayoutManager)
  - 일기 카드 (CardView)
    - 날짜 표시
    - 내용 미리보기 (최대 3줄)
    - 썸네일 이미지 (Glide)
    - 감정 아이콘
    - 태그 ChipGroup
  - Floating Action Button (+) - 우측 하단
  - SwipeRefreshLayout
- **기능**
  - 최신순 정렬
  - 무한 스크롤 페이지네이션 (20개씩)
  - 당겨서 새로고침
  - 카드 클릭 시 DiaryDetailActivity 이동
  - FAB 클릭 시 CreateDiaryActivity 이동

#### 2.2 주간 요약 (WeeklyDiaryListFragment)
- **UI 구성**
  - RecyclerView (LinearLayoutManager)
  - 주간 카드 (CardView)
    - 년도/주차 표시 (예: 2025년 11주차)
    - 날짜 범위 (시작일 ~ 종료일)
    - 주간 타이틀
    - 대표 이미지 (Glide)
    - 요약 텍스트 미리보기
- **기능**
  - 주별 자동 그룹화
  - AI 생성 요약 표시
  - 카드 클릭 시 WeeklyDiaryDetailActivity 이동

#### 2.3 검색 (SearchFragment)
- **UI 구성**
  - 검색어 입력 필드 (EditText)
  - 날짜 범위 선택 영역
    - 시작일 버튼 (DatePicker)
    - 종료일 버튼 (DatePicker)
  - 감정 필터 스피너 (Spinner)
    - 전체 감정, 기쁨, 슬픔, 분노, 놀람, 두려움, 중립
  - 태그 필터 스피너 (Spinner)
  - 검색 버튼
  - 검색 결과 RecyclerView
- **기능**
  - 다중 필터 적용
  - 실시간 검색
  - 검색 결과 페이지네이션 (20개씩)
  - 무한 스크롤
  - 검색 결과 카드 클릭 시 DiaryDetailActivity 이동

#### 2.4 프로필 (ProfileFragment)
- **UI 구성**
  - 프로필 이미지 영역 (CircleImageView)
  - 이메일 표시 (TextView, 수정 불가)
  - 닉네임 입력 필드 (EditText)
  - 닉네임 변경 버튼
  - 로그아웃 버튼
  - SwipeRefreshLayout
- **기능**
  - 사용자 정보 조회 (UserApi)
  - 닉네임 실시간 수정
  - 당겨서 새로고침
  - 로그아웃 확인 다이얼로그
  - 로그아웃 시 LoginActivity 이동 및 토큰 삭제

### 3. 상세 화면

#### CreateDiaryActivity - 일기 작성
- **UI 구성**
  - 상단 툴바 (뒤로가기 버튼)
  - 날짜 선택 영역 (Button + TextView)
    - DatePicker 다이얼로그
  - 사진 추가 영역 (ImageView + Placeholder)
    - 갤러리 접근
    - 선택된 이미지 미리보기
  - 내용 입력 필드 (EditText, 멀티라인)
  - 태그 선택 ChipGroup
    - 서버에서 태그 목록 로드
    - 다중 선택 가능
  - 저장 버튼
- **기능**
  - 갤러리 권한 요청 (READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE)
  - ActivityResultLauncher로 갤러리 접근
  - 이미지 URI 처리 및 미리보기
  - 태그 다중 선택 (Chip 상태 관리)
  - 입력 유효성 검사 (날짜, 내용 필수)
  - Multipart 파일 업로드
  - 저장 성공 시 MainActivity로 복귀 및 목록 새로고침

#### DiaryDetailActivity - 일기 상세
- **UI 구성**
  - 상단 툴바 (뒤로가기, 삭제 버튼)
  - 날짜 표시 (TextView)
  - 이미지 (ImageView, Glide)
    - 클릭 시 확대 보기
  - 내용 전체 표시 (TextView)
  - 태그 ChipGroup (읽기 전용)
  - AI 분석 결과 섹션
    - 비전 설명 (vision_description)
    - 생성된 스토리 (generated_story)
    - 전문가 코멘트 (expert_comment)
    - 감정 분석 결과 (emotion)
- **기능**
  - 일기 ID로 상세 정보 조회
  - 이미지 확대 보기
  - AI 분석 결과 표시 (있을 경우에만)
  - 삭제 확인 다이얼로그
  - 삭제 성공 시 이전 화면으로 복귀

#### WeeklyDiaryDetailActivity - 주간 다이어리 상세
- **UI 구성**
  - 상단 툴바 (뒤로가기 버튼)
  - 주차 정보 표시
    - 년도 (year)
    - 주차 (week_number)
    - 날짜 범위 (start_date ~ end_date)
  - 주간 타이틀 (weekly_title)
  - 대표 이미지 (weekly_image_url, Glide)
  - 주간 요약 텍스트 (weekly_summary_text)
  - 해당 주 일기 목록 (RecyclerView)
- **기능**
  - 주간 다이어리 ID로 상세 정보 조회
  - AI 생성 주간 요약 표시
  - 해당 주의 모든 일기 표시
  - 개별 일기 클릭 시 DiaryDetailActivity 이동

## 데이터 모델

### DiaryResponse
- diary_id: 일기 ID
- user_id: 사용자 ID
- date: 작성 날짜
- description: 일기 내용
- photo_url: 사진 URL
- vision_description: AI 이미지 분석 결과
- generated_story: AI 생성 스토리
- expert_comment: 전문가 코멘트
- emotion: 감정 (joy, sadness, anger, surprise, fear, neutral)
- year: 년도
- week_number: 주차
- tags: 태그 목록
- created_at: 생성 시간
- updated_at: 수정 시간

### WeeklyDiaryResponse
- week_id: 주간 다이어리 ID
- user_id: 사용자 ID
- year: 년도
- week_number: 주차
- start_date: 시작일
- end_date: 종료일
- weekly_summary_text: 주간 요약
- weekly_image_url: 대표 이미지 URL
- weekly_title: 주간 타이틀
- user_uploaded_image: 사용자 업로드 여부
- created_at: 생성 시간
- updated_at: 수정 시간

### Tag
- tag_id: 태그 ID
- tag_name: 태그 이름

## 네트워크 통신

### API 기본 정보
- **Base URL**: http://43.201.221.184:8080/api/
- **인증 방식**: JWT Bearer Token
- **데이터 포맷**: JSON
- **HTTP Client**: OkHttp3 + Retrofit2
- **이미지 업로드**: Multipart/form-data

### API 엔드포인트

#### AuthApi
- POST /auth/register - 회원가입
- POST /auth/login - 로그인

#### DiaryApi
- GET /diaries - 일기 목록 조회 (페이지네이션)
- GET /diaries/{id} - 일기 상세 조회
- POST /diaries - 일기 작성 (Multipart)
- DELETE /diaries/{id} - 일기 삭제
- GET /diaries/search - 일기 검색 (필터링)

#### WeeklyDiaryApi
- GET /weekly-diaries - 주간 다이어리 목록
- GET /weekly-diaries/{id} - 주간 다이어리 상세

#### TagApi
- GET /tags - 태그 목록 조회

#### UserApi
- GET /users/profile - 프로필 조회
- PUT /users/nickname - 닉네임 수정

## 의존성 라이브러리

### AndroidX Core
- androidx.core:core:1.12.0
- androidx.appcompat:appcompat:1.6.1
- androidx.activity:activity:1.8.2
- androidx.fragment:fragment:1.6.2

### UI Components
- com.google.android.material:material:1.11.0
- androidx.constraintlayout:constraintlayout:2.1.4
- androidx.recyclerview:recyclerview:1.3.2
- androidx.cardview:cardview:1.0.0
- androidx.swiperefreshlayout:swiperefreshlayout:1.1.0

### Lifecycle
- androidx.lifecycle:lifecycle-viewmodel:2.7.0
- androidx.lifecycle:lifecycle-livedata:2.7.0
- androidx.lifecycle:lifecycle-runtime:2.7.0

### Network
- com.squareup.retrofit2:retrofit:2.9.0
- com.squareup.retrofit2:converter-gson:2.9.0
- com.squareup.okhttp3:logging-interceptor:4.12.0

### JSON Parsing
- com.google.code.gson:gson:2.10.1

### Image Loading
- com.github.bumptech.glide:glide:4.16.0
- com.github.bumptech.glide:compiler:4.16.0 (annotation processor)

### Testing
- junit:junit:4.13.2
- androidx.test.ext:junit:1.1.5
- androidx.test.espresso:espresso-core:3.5.1

## 권한 요구사항

### 필수 권한
- INTERNET - 네트워크 통신
- ACCESS_NETWORK_STATE - 네트워크 상태 확인

### 런타임 권한
- READ_MEDIA_IMAGES (API 33+) - 갤러리 이미지 접근
- READ_EXTERNAL_STORAGE (API 32 이하) - 외부 저장소 읽기

## 빌드 및 실행

### 요구사항
- Android Studio Arctic Fox 이상
- JDK 8 이상
- Android SDK 34
- Gradle 8.x

### 실행 방법
1. Android Studio에서 프로젝트 열기
2. Gradle 동기화 실행 (Sync Project with Gradle Files)
3. 에뮬레이터 또는 실제 기기 연결
4. Run 버튼 클릭 또는 Shift + F10

### 빌드 구성
- compileSdk: 34
- minSdk: 24
- targetSdk: 34
- Java Version: 1.8
- ViewBinding: 활성화
