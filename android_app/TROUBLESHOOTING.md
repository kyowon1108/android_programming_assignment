# Android 빌드 문제 해결

## Gson import 에러 (`com.google.gson.annotations.SerializedName`)

### 증상
```
import com.google.gson.annotations.SerializedName;
```
위 import문에서 빨간 밑줄 또는 "Cannot resolve symbol" 에러 발생

### 해결 방법

#### 1. Gradle Sync 실행 (가장 일반적인 해결책)

Android Studio에서:
```
File > Sync Project with Gradle Files
```

또는 Gradle 탭에서:
```
오른쪽 상단 Gradle 아이콘 클릭 > Sync 버튼 클릭
```

#### 2. Gradle Clean & Rebuild

```bash
cd android_app
./gradlew clean
./gradlew build
```

Android Studio에서:
```
Build > Clean Project
Build > Rebuild Project
```

#### 3. Gradle 캐시 삭제 (심각한 경우)

```bash
cd android_app
rm -rf .gradle
rm -rf app/build
./gradlew clean
./gradlew build
```

#### 4. Android Studio 캐시 무효화

```
File > Invalidate Caches / Restart... > Invalidate and Restart
```

#### 5. 인터넷 연결 확인

Gradle이 Maven Central에서 라이브러리를 다운로드하지 못할 수 있습니다.
- 인터넷 연결 확인
- 프록시 설정 확인 (회사/학교 네트워크)

#### 6. build.gradle.kts 확인

`android_app/app/build.gradle.kts` 파일에 다음이 있는지 확인:

```kotlin
dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### 추가 확인 사항

#### Gradle 버전 확인
`android_app/gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

#### JDK 버전 확인
```bash
java -version
# OpenJDK 11 이상 권장
```

Android Studio에서:
```
File > Project Structure > SDK Location > JDK location
```

## 기타 일반적인 빌드 에러

### "Manifest merger failed"
- `android_app/app/src/main/AndroidManifest.xml` 문법 확인
- 중복된 권한이나 Activity 선언 확인

### "Resource not found"
- R.layout.xxx, R.drawable.xxx 등이 없는 경우
- `res/` 디렉토리 확인
- Gradle Sync 실행

### "Cannot resolve symbol R"
```
Build > Clean Project
Build > Rebuild Project
```

## 명령줄에서 빌드 (디버깅용)

```bash
cd android_app

# Debug APK 빌드
./gradlew assembleDebug

# APK 위치
# app/build/outputs/apk/debug/app-debug.apk

# 의존성 확인
./gradlew app:dependencies

# 빌드 정보 출력
./gradlew build --info
```

## Android Studio 권장 설정

- **Gradle JDK**: JDK 11 or 17
- **Android SDK**: API 34 (Android 14)
- **Minimum SDK**: API 24 (Android 7.0)
- **Build Tools**: 최신 버전

## 완전 초기화 (최후의 수단)

```bash
cd android_app

# 모든 빌드 파일 삭제
rm -rf .gradle
rm -rf .idea
rm -rf app/build
rm -rf build

# Gradle wrapper 재생성
./gradlew wrapper --gradle-version 8.2

# 프로젝트 다시 열기
# Android Studio > Open > android_app 폴더 선택
```
