# Baby Diary Android App

## 개요
아이의 일상을 AI 그림동화로 기록하는 Android 애플리케이션

## 기술 스택
- **SDK**: Android API 24+ (Android 7.0 Nougat)
- **언어**: Java
- **빌드 도구**: Gradle 8.2.0 (Kotlin DSL)
- **로컬 DB**: SQLite
- **UI**: Android SDK (XML Layout)
- **네트워크**: HttpsURLConnection
- **이미지 로딩**: Glide 4.16.0
- **JSON 파싱**: Gson 2.10.1

---

## 프로젝트 구조

```
app/src/main/
├── java/com/example/babydiary/
│   ├── activity/           # 화면 (Activities)
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── MainActivity.java
│   │   ├── CreateDiaryActivity.java
│   │   ├── DiaryDetailActivity.java
│   │   ├── WeeklyDiaryActivity.java
│   │   └── SettingsActivity.java
│   │
│   ├── service/            # API 통신 서비스
│   │   ├── AuthService.java
│   │   ├── DiaryService.java
│   │   ├── WeeklyDiaryService.java
│   │   ├── TagService.java
│   │   └── ApiClient.java
│   │
│   ├── database/           # SQLite 로컬 DB
│   │   ├── DatabaseHelper.java
│   │   ├── DiaryDao.java
│   │   ├── UserDao.java
│   │   └── TagDao.java
│   │
│   ├── model/              # 데이터 모델
│   │   ├── User.java
│   │   ├── Diary.java
│   │   ├── Tag.java
│   │   ├── WeeklyDiary.java
│   │   └── ApiResponse.java
│   │
│   ├── adapter/            # RecyclerView 어댑터
│   │   ├── DiaryAdapter.java
│   │   ├── WeeklyDiaryAdapter.java
│   │   ├── TagAdapter.java
│   │   └── EmotionAdapter.java
│   │
│   ├── util/               # 유틸리티 클래스
│   │   ├── Constants.java          ✅ 완료
│   │   ├── SharedPrefsManager.java
│   │   ├── ImageUtils.java
│   │   ├── DateUtils.java
│   │   ├── NetworkUtils.java
│   │   ├── PermissionUtils.java
│   │   └── ValidationUtils.java
│   │
│   ├── listener/           # 인터페이스/콜백
│   │   ├── OnDiaryClickListener.java
│   │   └── OnApiResponseListener.java
│   │
│   └── dialog/             # 커스텀 다이얼로그
│       ├── LoadingDialog.java
│       ├── ImagePickerDialog.java
│       └── TagSelectionDialog.java
│
└── res/
    ├── layout/             # XML 레이아웃
    ├── drawable/           # 이미지/벡터
    ├── values/             # 문자열/색상/테마
    ├── mipmap/             # 앱 아이콘
    ├── menu/               # 메뉴
    ├── raw/                # BGM (mp3)
    └── xml/                # 설정 파일
        └── network_security_config.xml  ✅ 완료
```

---

## 빌드 및 실행

### 사전 요구사항
- Android Studio Hedgehog (2023.1.1) 이상
- JDK 11 이상
- Android SDK 34
- Backend API 서버 실행 중 (http://localhost:8000)

### 1. Android Studio에서 프로젝트 열기
```bash
# Android Studio > Open > android_app 폴더 선택
```

### 2. Gradle Sync
```
File > Sync Project with Gradle Files
```

### 3. 에뮬레이터 또는 실제 기기 준비

#### 에뮬레이터
- AVD Manager에서 API 24+ 에뮬레이터 생성
- Backend API: `http://10.0.2.2:8000` (자동 설정됨)

#### 실제 기기
1. USB 디버깅 활성화
2. PC와 같은 Wi-Fi 네트워크 연결
3. PC의 IP 확인:
   ```bash
   # macOS/Linux
   ifconfig | grep "inet "

   # Windows
   ipconfig
   ```
4. `Constants.java` 수정:
   ```java
   private static final String BASE_URL_DEVICE = "http://YOUR_PC_IP:8000";
   ```

### 4. 빌드 및 실행
```bash
# 커맨드 라인
./gradlew clean build
./gradlew installDebug

# 또는 Android Studio에서 Run 버튼 클릭
```

---

## 환경 설정

### 이미 완료된 설정 ✅

#### 1. AndroidManifest.xml 권한
```xml
✅ INTERNET - API 통신
✅ CAMERA - 사진 촬영
✅ READ_EXTERNAL_STORAGE - 갤러리 접근
✅ WRITE_EXTERNAL_STORAGE - 파일 저장
✅ READ_MEDIA_IMAGES - Android 13+ 이미지 접근
✅ ACCESS_NETWORK_STATE - 네트워크 상태 확인
```

#### 2. build.gradle.kts 의존성
```kotlin
✅ androidx.constraintlayout:2.1.4
✅ androidx.recyclerview:1.3.2
✅ androidx.cardview:1.0.0
✅ com.google.code.gson:2.10.1
✅ com.github.bumptech.glide:4.16.0
```

#### 3. 네트워크 보안 설정
```xml
✅ network_security_config.xml - localhost HTTP 허용
```

#### 4. 패키지 구조
```
✅ activity/
✅ service/
✅ database/
✅ model/
✅ adapter/
✅ util/ (Constants.java 완료)
✅ listener/
✅ dialog/
```

---

## 개발 가이드

### 1. API 통신 패턴

#### HttpsURLConnection 사용
```java
// 예시: AuthService.java
public class AuthService {
    public void login(String email, String password, OnApiResponseListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.getFullUrl(Constants.ENDPOINT_LOGIN));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                // JSON 요청 바디
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", email);
                jsonBody.put("password", password);

                // 전송
                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.close();

                // 응답
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                    String response = reader.readLine();

                    // UI 스레드에서 콜백
                    new Handler(Looper.getMainLooper()).post(() -> {
                        listener.onSuccess(response);
                    });
                }
            } catch (Exception e) {
                // 에러 처리
            }
        }).start();
    }
}
```

### 2. SharedPreferences 사용
```java
// 토큰 저장
SharedPrefsManager.saveToken(context, token);
SharedPrefsManager.saveUserId(context, userId);

// 토큰 가져오기
String token = SharedPrefsManager.getToken(context);
```

### 3. 이미지 업로드
```java
// MultipartFormData 사용
// 추후 ImageUtils.java에서 구현
```

### 4. 권한 요청
```java
// PermissionUtils.java 사용
if (PermissionUtils.checkCameraPermission(this)) {
    // 카메라 사용
} else {
    PermissionUtils.requestCameraPermission(this);
}
```

---

## 주요 기능 구현 계획

### Phase 1: 인증 시스템 ✅ (진행 예정)
- [ ] LoginActivity (로그인 화면)
- [ ] RegisterActivity (회원가입 화면)
- [ ] AuthService (API 통신)
- [ ] SharedPrefsManager (토큰 저장)

### Phase 2: 다이어리 CRUD
- [ ] MainActivity (홈 화면, 다이어리 목록)
- [ ] CreateDiaryActivity (다이어리 작성)
- [ ] DiaryDetailActivity (다이어리 상세)
- [ ] DiaryService (API 통신)
- [ ] DiaryAdapter (RecyclerView)
- [ ] ImageUtils (사진 촬영/선택/압축)

### Phase 3: 주간 다이어리
- [ ] WeeklyDiaryActivity
- [ ] WeeklyDiaryService
- [ ] WeeklyDiaryAdapter
- [ ] BGM 재생 기능

### Phase 4: 검색 및 필터
- [ ] 태그별 검색
- [ ] 연도/주차 필터
- [ ] 감정별 필터

### Phase 5: SQLite 로컬 캐싱
- [ ] DatabaseHelper
- [ ] DiaryDao (CRUD)
- [ ] 오프라인 모드 지원

---

## API 엔드포인트

모든 API 엔드포인트는 `Constants.java`에 정의되어 있습니다.

### 인증
```java
Constants.ENDPOINT_REGISTER  // POST /api/v1/auth/register
Constants.ENDPOINT_LOGIN     // POST /api/v1/auth/login
Constants.ENDPOINT_ME        // GET  /api/v1/auth/me
Constants.ENDPOINT_REFRESH   // POST /api/v1/auth/refresh
```

### 다이어리
```java
Constants.ENDPOINT_DIARIES              // GET/POST /api/v1/diaries
Constants.ENDPOINT_DIARY_BY_ID          // GET/DELETE /api/v1/diaries/{id}
```

### 주간 다이어리
```java
Constants.ENDPOINT_WEEKLY_DIARIES       // GET/POST /api/v1/weekly_diaries
Constants.ENDPOINT_WEEKLY_DIARY_BY_ID   // GET /api/v1/weekly_diaries/{id}
Constants.ENDPOINT_WEEKLY_DIARY_BY_DATE // GET /api/v1/weekly_diaries/by-date/{year}/{week}
```

### 태그
```java
Constants.ENDPOINT_TAGS                 // GET /api/v1/tags
Constants.ENDPOINT_TAG_CATEGORIES       // GET /api/v1/tags/categories
Constants.ENDPOINT_TAG_BY_CATEGORY      // GET /api/v1/tags/category/{category}
```

자세한 API 명세는 [/docs/API_SPEC.md](../docs/API_SPEC.md) 참고

---

## 트러블슈팅

### 1. Backend 연결 실패
```
에러: java.net.ConnectException: Failed to connect to /10.0.2.2:8000
```
**해결**: Backend 서버가 실행 중인지 확인
```bash
cd backend
python main.py
```

### 2. 권한 거부 (Permission Denied)
```
에러: Camera permission denied
```
**해결**: 런타임 권한 요청 구현 필요 (PermissionUtils 사용)

### 3. JSON 파싱 에러
```
에러: com.google.gson.JsonSyntaxException
```
**해결**: API 응답 형식 확인, 모델 클래스 필드 확인

### 4. 이미지 업로드 실패
```
에러: java.io.FileNotFoundException
```
**해결**:
- 파일 경로 확인
- READ_EXTERNAL_STORAGE 권한 확인
- Android 10+ (API 29)에서는 Scoped Storage 사용

### 5. Gradle Sync 실패
```bash
./gradlew clean
./gradlew --refresh-dependencies
```

---

## 테스트

### Unit Test
```bash
./gradlew test
```

### Instrumented Test (에뮬레이터/기기 필요)
```bash
./gradlew connectedAndroidTest
```

---

## 참고 문서
- [Backend API 명세](../docs/API_SPEC.md)
- [프로젝트 전체 구조](../documentation/integrated-repo-structure.md)
- [커밋 규칙](../documentation/templates/commit-template.md)

---

## 개발 우선순위

1. **Model 클래스** (model/)
2. **API 서비스** (service/)
3. **유틸리티** (util/)
4. **인증 화면** (LoginActivity, RegisterActivity)
5. **메인 화면** (MainActivity)
6. **다이어리 작성** (CreateDiaryActivity)
7. **나머지 기능**

---

## 라이선스
MIT

## 개발자
kyowon1108
