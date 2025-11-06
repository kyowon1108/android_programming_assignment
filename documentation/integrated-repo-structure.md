# 애기 다이어리 & AI 그림동화 앱 - 통합 프로젝트 구조 가이드

## 목차
1. [전체 프로젝트 구조](#전체-프로젝트-구조)
2. [Android App 폴더 설정](#android-app-폴더-설정)
3. [Backend 폴더 설정](#backend-폴더-설정)
4. [Database 폴더 설정](#database-폴더-설정)
5. [GitHub 레포지토리 커밋 가이드](#github-레포지토리-커밋-가이드)
6. [개발 환경 설정](#개발-환경-설정)
7. [주요 구성 파일](#주요-구성-파일)

---

## 전체 프로젝트 구조

```
android_programming_assignment/                    # GitHub 루트
├── .git/
├── .gitignore                                     # 최상위 .gitignore
├── README.md                                      # 프로젝트 소개 (통합)
├── LICENSE                                        # MIT License
│
├── android_app/                                   # Android 애플리케이션
│   ├── .gitignore                                # Android 전용 .gitignore
│   ├── README.md                                 # Android 설정 및 빌드 가이드
│   ├── build.gradle                              # 프로젝트 레벨 gradle
│   ├── settings.gradle
│   ├── gradle.properties
│   │
│   └── app/
│       ├── build.gradle
│       ├── proguard-rules.pro
│       └── src/
│           ├── main/
│           │   ├── AndroidManifest.xml
│           │   ├── java/com/example/babydiary/
│           │   │   ├── BabyDiaryApplication.java
│           │   │   ├── activity/ (7개 파일)
│           │   │   ├── service/ (5개 파일)
│           │   │   ├── database/ (4개 파일)
│           │   │   ├── model/ (5개 파일)
│           │   │   ├── adapter/ (4개 파일)
│           │   │   ├── util/ (7개 파일)
│           │   │   ├── listener/ (2개 파일)
│           │   │   └── dialog/ (3개 파일)
│           │   │
│           │   └── res/
│           │       ├── layout/ (13개 XML)
│           │       ├── drawable/ (9개)
│           │       ├── values/ (4개 XML)
│           │       ├── mipmap/
│           │       ├── menu/
│           │       ├── raw/ (BGM 4개 mp3)
│           │       └── font/
│           │
│           └── test/
│               └── java/com/example/babydiary/
│
├── backend/                                       # 백엔드 서버
│   ├── .gitignore                                # Node/Python 전용 .gitignore
│   ├── README.md                                 # 백엔드 설정 가이드
│   ├── .env.example                              # 환경변수 예시 (실제 .env는 .gitignore)
│   │
│   ├── [Node.js 버전]
│   ├── package.json
│   ├── package-lock.json
│   ├── node_modules/ (.gitignore)
│   │
│   ├── [또는 Python 버전]
│   ├── requirements.txt
│   ├── venv/ (.gitignore)
│   │
│   ├── config/
│   │   ├── database.js (또는 .py)
│   │   ├── api_keys.js (또는 .py)
│   │   └── constants.js (또는 .py)
│   │
│   ├── routes/
│   │   ├── auth.js (또는 .py)
│   │   ├── diaries.js (또는 .py)
│   │   ├── tags.js (또는 .py)
│   │   └── weekly_diaries.js (또는 .py)
│   │
│   ├── controllers/
│   │   ├── authController.js (또는 .py)
│   │   ├── diaryController.js (또는 .py)
│   │   ├── tagController.js (또는 .py)
│   │   └── weeklyDiaryController.js (또는 .py)
│   │
│   ├── middleware/
│   │   ├── auth.js (또는 .py)
│   │   ├── errorHandler.js (또는 .py)
│   │   └── validation.js (또는 .py)
│   │
│   ├── services/
│   │   ├── visionService.js (또는 .py)       # Google Vision API
│   │   ├── geminiService.js (또는 .py)       # Google Gemini API
│   │   ├── imageGenerationService.js (또는 .py)
│   │   └── streakService.js (또는 .py)
│   │
│   ├── utils/
│   │   ├── validators.js (또는 .py)
│   │   ├── helpers.js (또는 .py)
│   │   └── logger.js (또는 .py)
│   │
│   ├── uploads/                                  # 임시 업로드 파일 (.gitignore)
│   │
│   └── app.js (또는 main.py)                    # 엔트리 포인트
│
├── database/                                      # 데이터베이스 스크립트
│   ├── README.md                                 # DB 설정 가이드
│   ├── schemas/
│   │   ├── users.sql
│   │   ├── diaries.sql
│   │   ├── tags.sql
│   │   ├── diary_tags.sql
│   │   └── weekly_diaries.sql
│   │
│   ├── migrations/                               # DB 마이그레이션 (향후)
│   │   └── 001_initial_schema.sql
│   │
│   ├── seeds/                                    # 초기 데이터
│   │   └── sample_tags.sql
│   │
│   └── init.sql                                  # 전체 DB 초기화 스크립트
│
├── docs/                                         # 프로젝트 문서
│   ├── API_SPEC.md                               # API 명세 (통합)
│   ├── DATABASE_SCHEMA.md                        # DB 스키마 설명
│   ├── ARCHITECTURE.md                           # 전체 아키텍처
│   ├── SETUP_GUIDE.md                            # 개발 환경 설정
│   ├── DEVELOPMENT_FLOW.md                       # 개발 흐름
│   └── TESTING_GUIDE.md                          # 테스트 가이드
│
├── .github/                                      # GitHub 자동화
│   └── workflows/                                # CI/CD (선택사항)
│       └── build.yml
│
└── .env.example                                  # 최상위 환경변수 예시

```

---

## Android App 폴더 설정

### 기존 android_app 유지 및 수정 사항

#### 1. android_app/.gitignore (Android 전용)

```
# Gradle
.gradle/
build/
*.apk
*.ap_
*.dex
*.class

# IDE
.idea/
*.iml
*.swp
*.swo
*~

# 환경
local.properties

# 로그
*.log

# OS
.DS_Store
Thumbs.db
```

#### 2. android_app/build.gradle (프로젝트 레벨)

```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext {
        compileSdkVersion = 34
        minSdkVersion = 24
        targetSdkVersion = 34
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

#### 3. android_app/app/build.gradle (앱 레벨)

```gradle
plugins {
    id 'com.android.application'
}

android {
    compileSdk 34

    defaultConfig {
        applicationId "com.example.babydiary"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
}

dependencies {
    // Android 기본
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.9.0'

    // 네트워킹
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    // 또는: implementation 'com.squareup.retrofit2:retrofit:2.9.0'

    // 이미지 처리 (선택)
    // implementation 'com.github.bumptech.glide:glide:4.15.1'

    // JSON 파싱 (선택)
    // implementation 'com.google.code.gson:gson:2.10.1'

    // 테스트
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

#### 4. android_app/README.md (Android 전용)

```markdown
# Baby Diary Android App

## 개요
아이의 일상을 AI 그림동화로 기록하는 Android 애플리케이션

## 기술 스택
- **SDK**: Android API 24 (Android 7.0)
- **언어**: Java
- **로컬 DB**: SQLite
- **UI**: Android SDK (XML Layout)
- **네트워크**: HttpsURLConnection / OkHttp

## 빌드 및 실행

### 사전 요구사항
- Android Studio 최신 버전
- JDK 11 이상
- Android SDK 34

### 빌드
```bash
cd android_app
./gradlew build
```

### 에뮬레이터에서 실행
```bash
./gradlew installDebug
```

### 기기에서 실행
1. USB 디버깅 활성화
2. 기기 연결
3. `./gradlew installDebug`

## 프로젝트 구조

```
app/src/
├── main/
│   ├── java/com/example/babydiary/
│   │   ├── activity/          # 화면 (7개)
│   │   ├── service/           # API 호출 (5개)
│   │   ├── database/          # SQLite (4개)
│   │   ├── model/             # 데이터 모델 (5개)
│   │   ├── adapter/           # 어댑터 (4개)
│   │   ├── util/              # 유틸리티 (7개)
│   │   ├── listener/          # 리스너 (2개)
│   │   └── dialog/            # 다이얼로그 (3개)
│   │
│   └── res/
│       ├── layout/            # XML 레이아웃 (13개)
│       ├── drawable/          # 이미지/벡터 (9개)
│       ├── values/            # 문자열/색상 (4개)
│       ├── mipmap/            # 앱 아이콘
│       ├── menu/              # 메뉴 파일
│       ├── raw/               # BGM 음악 (4개 mp3)
│       └── font/              # 커스텀 폰트
```

## 환경 설정

### API 키 설정
1. `app/build.gradle`에서 API 기본 URL 설정
2. `Constants.java`에서 API 엔드포인트 정의

## 주요 기능

### 1. 데일리 다이어리
- 3줄 텍스트 + 1장 사진
- Vision API로 사진 설명 추출
- Gemini API로 동화/감정/전문가 의견 생성

### 2. 주간 다이어리
- 7일 다이어리 종합
- AI 제목/요약 생성
- BGM 재생

### 3. 검색/필터
- 태그별 검색
- 연도/주차별 필터

### 4. 연속 작성 추적
- Best Streak / Current Streak

## 테스트

### Unit Test
```bash
./gradlew test
```

### Instrumented Test
```bash
./gradlew connectedAndroidTest
```

## 주의사항
- 모든 네트워크 작업은 백그라운드 스레드에서 처리
- UI 업데이트는 메인 스레드에서만 처리
- 권한(카메라, 스토리지) 런타임 확인 필수

## 문제 해결

### 빌드 오류
- Gradle 캐시 삭제: `./gradlew clean`
- 의존성 재다운로드: `./gradlew --refresh-dependencies`

### 실행 오류
- 에뮬레이터 다시 시작
- 에뮬레이터에서 앱 캐시 삭제
- 기기 재부팅

## 개발자
kyowon1108

## 라이선스
MIT
```

---

## Backend 폴더 설정

### Node.js 버전 (Express.js)

#### 1. backend/.env.example

```
# 서버 설정
NODE_ENV=development
PORT=3000
HOST=localhost

# 데이터베이스
DB_HOST=localhost
DB_PORT=5432
DB_NAME=baby_diary
DB_USER=postgres
DB_PASSWORD=your_password

# JWT 인증
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=7d

# Google API
GOOGLE_VISION_API_KEY=your_vision_api_key
GOOGLE_GEMINI_API_KEY=your_gemini_api_key

# 파일 업로드
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=10485760

# CORS
CORS_ORIGIN=http://localhost:8080

# 로깅
LOG_LEVEL=debug
```

#### 2. backend/package.json

```json
{
  "name": "baby-diary-backend",
  "version": "1.0.0",
  "description": "Backend API for Baby Diary AI App",
  "main": "app.js",
  "scripts": {
    "start": "node app.js",
    "dev": "nodemon app.js",
    "test": "jest --detectOpenHandles",
    "lint": "eslint .",
    "db:init": "node database/init.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "express-cors": "^0.0.7",
    "pg": "^8.11.3",
    "dotenv": "^16.3.1",
    "jsonwebtoken": "^9.1.1",
    "bcryptjs": "^2.4.3",
    "axios": "^1.6.2",
    "multer": "^1.4.5-lts.1",
    "joi": "^17.11.0",
    "winston": "^3.11.0",
    "uuid": "^9.0.1"
  },
  "devDependencies": {
    "nodemon": "^3.0.2",
    "jest": "^29.7.0",
    "eslint": "^8.54.0"
  },
  "engines": {
    "node": ">=14.0.0",
    "npm": ">=6.0.0"
  }
}
```

#### 3. backend/app.js

```javascript
const express = require('express');
const cors = require('express-cors');
const dotenv = require('dotenv');
const path = require('path');

// 환경 변수 로드
dotenv.config();

// Express 앱 초기화
const app = express();

// 미들웨어
app.use(express.json());
app.use(express.urlencoded({ limit: '10mb', extended: true }));
app.use(cors());

// 정적 파일
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// 라우트
app.use('/api/v1/auth', require('./routes/auth'));
app.use('/api/v1/diaries', require('./routes/diaries'));
app.use('/api/v1/tags', require('./routes/tags'));
app.use('/api/v1/weekly_diaries', require('./routes/weekly_diaries'));
app.use('/api/v1/users', require('./routes/users'));

// 에러 핸들러
app.use(require('./middleware/errorHandler'));

// 404 핸들러
app.use((req, res) => {
    res.status(404).json({ error: 'Not Found' });
});

// 서버 시작
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});

module.exports = app;
```

#### 4. backend/routes/auth.js

```javascript
const express = require('express');
const authController = require('../controllers/authController');
const authMiddleware = require('../middleware/auth');

const router = express.Router();

// 회원가입
router.post('/register', authController.register);

// 로그인
router.post('/login', authController.login);

// 로그아웃 (인증 필요)
router.post('/logout', authMiddleware, authController.logout);

module.exports = router;
```

#### 5. backend/config/database.js

```javascript
const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    database: process.env.DB_NAME,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
});

pool.on('error', (err) => {
    console.error('Unexpected error on idle client', err);
});

module.exports = pool;
```

#### 6. backend/middleware/auth.js

```javascript
const jwt = require('jsonwebtoken');

const authMiddleware = (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];
        
        if (!token) {
            return res.status(401).json({ error: 'No token provided' });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        res.status(401).json({ error: 'Invalid token' });
    }
};

module.exports = authMiddleware;
```

#### 7. backend/services/geminiService.js

```javascript
const axios = require('axios');

class GeminiService {
    constructor() {
        this.apiKey = process.env.GOOGLE_GEMINI_API_KEY;
        this.baseUrl = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent';
    }

    async generateStory(visionDescription, diaryDescription) {
        const prompt = `다음은 아이의 사진 설명과 일기 내용입니다. 
이를 바탕으로 5~7세 아이를 위한 짧은 그림동화를 500자 이내로 만들어주세요.

사진 설명: ${visionDescription}
일기: ${diaryDescription}`;

        return await this.callGemini(prompt);
    }

    async analyzeEmotion(diaryDescription) {
        const prompt = `다음 아이 일기의 감정을 분석하세요. 
다음 중 하나로만 답변: joy, sadness, anger, surprise, fear, neutral

일기: ${diaryDescription}`;

        return await this.callGemini(prompt);
    }

    async generateExpertComment(diaryDescription, emotion) {
        const prompt = `아이 일기를 읽은 육아전문가로서 부모에게 칭찬과 조언을 300자 이내로 작성해주세요.

감정: ${emotion}
일기: ${diaryDescription}`;

        return await this.callGemini(prompt);
    }

    async callGemini(prompt) {
        try {
            const response = await axios.post(
                `${this.baseUrl}?key=${this.apiKey}`,
                {
                    contents: [
                        {
                            role: 'user',
                            parts: [{ text: prompt }]
                        }
                    ]
                }
            );

            return response.data.candidates[0].content.parts[0].text;
        } catch (error) {
            console.error('Gemini API Error:', error.message);
            throw new Error('Failed to generate content');
        }
    }
}

module.exports = new GeminiService();
```

#### 8. backend/services/visionService.js

```javascript
const vision = require('@google-cloud/vision');

const visionClient = new vision.ImageAnnotatorClient({
    keyFilename: process.env.GOOGLE_VISION_KEY_FILE
});

class VisionService {
    async extractTextFromImage(imageBase64) {
        try {
            const request = {
                image: { content: imageBase64 },
                features: [{ type: 'TEXT_DETECTION' }]
            };

            const [result] = await visionClient.annotateImage(request);
            const fullTextAnnotation = result.fullTextAnnotation;

            return fullTextAnnotation?.text || '';
        } catch (error) {
            console.error('Vision API Error:', error.message);
            throw new Error('Failed to extract text from image');
        }
    }

    async labelImageContent(imageBase64) {
        try {
            const request = {
                image: { content: imageBase64 },
                features: [{ type: 'LABEL_DETECTION' }]
            };

            const [result] = await visionClient.annotateImage(request);
            const labels = result.labelAnnotations || [];

            return labels.map(label => label.description).join(', ');
        } catch (error) {
            console.error('Vision API Error:', error.message);
            throw new Error('Failed to label image');
        }
    }
}

module.exports = new VisionService();
```

#### 9. backend/README.md

```markdown
# Baby Diary Backend API

## 개요
Android 앱을 위한 REST API 서버

## 기술 스택
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: PostgreSQL
- **인증**: JWT
- **외부 API**: Google Vision API, Google Gemini API

## 설치

### 사전 요구사항
- Node.js 14+
- PostgreSQL 12+
- npm

### 설치 스텝
```bash
cd backend
npm install
cp .env.example .env
# .env 파일 수정 (API 키 등)
npm run db:init
```

## 실행

### 개발 모드
```bash
npm run dev
```

### 프로덕션 모드
```bash
npm start
```

## API 엔드포인트

### 인증
- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/auth/logout` - 로그아웃

### 다이어리
- `POST /api/v1/diaries` - 다이어리 생성
- `GET /api/v1/diaries/:user_id` - 다이어리 목록
- `GET /api/v1/diaries/:diary_id` - 다이어리 상세
- `PUT /api/v1/diaries/:diary_id` - 다이어리 수정
- `DELETE /api/v1/diaries/:diary_id` - 다이어리 삭제

### 주간 다이어리
- `POST /api/v1/weekly_diaries` - 주간 다이어리 생성
- `GET /api/v1/weekly_diaries/:user_id` - 주간 목록
- `GET /api/v1/weekly_diaries/:week_id/full` - 주간 상세

## 테스트

### Unit Test
```bash
npm test
```

## 배포
AWS/Heroku/DigitalOcean 등에 배포 가능

## 라이선스
MIT
```

---

## Database 폴더 설정

### 1. database/schemas/users.sql

```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500),
    best_streak INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
```

### 2. database/schemas/diaries.sql

```sql
CREATE TABLE diaries (
    diary_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    date DATE NOT NULL,
    description TEXT NOT NULL,
    photo_url VARCHAR(500) NOT NULL,
    vision_description TEXT,
    generated_story TEXT,
    expert_comment TEXT,
    emotion VARCHAR(50),
    year INTEGER NOT NULL,
    week_number INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_diaries_user_date ON diaries(user_id, date);
CREATE INDEX idx_diaries_user_year_week ON diaries(user_id, year, week_number);
CREATE INDEX idx_diaries_emotion ON diaries(emotion);
```

### 3. database/schemas/tags.sql

```sql
CREATE TABLE tags (
    tag_id SERIAL PRIMARY KEY,
    tag_name VARCHAR(100) UNIQUE NOT NULL,
    tag_category VARCHAR(50)
);

INSERT INTO tags (tag_name, tag_category) VALUES
('기쁨', 'emotion'),
('슬픔', 'emotion'),
('화남', 'emotion'),
('놀람', 'emotion'),
('두려움', 'emotion'),
('성장', 'development'),
('우정', 'relationship'),
('감동', 'emotion'),
('기억', 'memory'),
('건강', 'health');
```

### 4. database/schemas/diary_tags.sql

```sql
CREATE TABLE diary_tags (
    diary_id INTEGER NOT NULL REFERENCES diaries(diary_id) ON DELETE CASCADE,
    tag_id INTEGER NOT NULL REFERENCES tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (diary_id, tag_id)
);

CREATE INDEX idx_diary_tags_tag_id ON diary_tags(tag_id);
```

### 5. database/schemas/weekly_diaries.sql

```sql
CREATE TABLE weekly_diaries (
    week_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    year INTEGER NOT NULL,
    week_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    weekly_summary_text TEXT,
    weekly_image_url VARCHAR(500),
    weekly_title VARCHAR(200),
    user_uploaded_image BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, year, week_number)
);

CREATE INDEX idx_weekly_diaries_user_year_week ON weekly_diaries(user_id, year, week_number);
```

### 6. database/init.sql (전체 초기화)

```sql
-- 모든 테이블 생성
\i schemas/users.sql
\i schemas/diaries.sql
\i schemas/tags.sql
\i schemas/diary_tags.sql
\i schemas/weekly_diaries.sql

-- 초기 데이터 삽입 (선택사항)
\i seeds/sample_tags.sql

-- 연결 종료 전 확인 메시지
SELECT 'Database initialization completed!' as message;
```

### 7. database/seeds/sample_tags.sql

```sql
-- 기본 태그 (tags 테이블에 이미 있음, 참고용)
-- 필요한 경우 추가 태그 여기서 정의
```

### 8. database/README.md

```markdown
# Database Scripts

## 개요
PostgreSQL 데이터베이스 초기화 및 마이그레이션 스크립트

## 환경 설정

### PostgreSQL 설치
```bash
# macOS
brew install postgresql

# Ubuntu
sudo apt-get install postgresql

# Windows
# https://www.postgresql.org/download/windows/ 에서 설치
```

### 초기화

```bash
# PostgreSQL 서비스 시작
sudo service postgresql start

# DB 생성
createdb baby_diary

# 테이블 생성
psql -U postgres -d baby_diary -f init.sql

# 확인
psql -U postgres -d baby_diary -c "\\dt"
```

## 테이블 구조

### users
- user_id: 사용자 ID
- email: 이메일 (고유)
- password_hash: 비밀번호 (해시)
- nickname: 닉네임
- best_streak, current_streak: 연속 작성 기록

### diaries
- diary_id: 다이어리 ID
- user_id: 사용자 ID (FK)
- date: 작성 날짜
- description: 사용자 입력 텍스트
- photo_url: 사진 URL
- vision_description: Vision API 결과
- generated_story: Gemini 생성 동화
- expert_comment: 전문가 의견
- emotion: 감정 분석 결과

### tags
- tag_id: 태그 ID
- tag_name: 태그명
- tag_category: 카테고리

### diary_tags (M:N)
- diary_id, tag_id: 다이어리와 태그의 관계

### weekly_diaries
- week_id: 주간 다이어리 ID
- user_id: 사용자 ID (FK)
- year, week_number: 연도/주차
- weekly_summary_text: 주간 요약
- weekly_image_url: 주간 대표 이미지
- weekly_title: 주간 제목
- user_uploaded_image: 사용자 업로드 여부

## 백업 및 복구

### 백업
```bash
pg_dump -U postgres baby_diary > backup.sql
```

### 복구
```bash
psql -U postgres baby_diary < backup.sql
```

## 라이선스
MIT
```

---

## GitHub 레포지토리 커밋 가이드

### Step 1: 폴더 구조 생성

```bash
cd /path/to/android_programming_assignment

# 기존 폴더 확인
ls -la

# backend, database 폴더 생성 (android_app은 이미 존재)
mkdir -p backend/config backend/routes backend/controllers backend/middleware backend/services backend/utils backend/uploads
mkdir -p database/schemas database/migrations database/seeds
mkdir -p docs
```

### Step 2: 파일 생성 및 배치

```bash
# backend 파일 생성
touch backend/.env.example
touch backend/package.json
touch backend/app.js
touch backend/README.md
touch backend/config/database.js
touch backend/config/api_keys.js
touch backend/config/constants.js
touch backend/routes/auth.js
touch backend/routes/diaries.js
touch backend/routes/tags.js
touch backend/routes/weekly_diaries.js
touch backend/routes/users.js
touch backend/middleware/auth.js
touch backend/middleware/errorHandler.js
touch backend/services/visionService.js
touch backend/services/geminiService.js

# database 파일 생성
touch database/init.sql
touch database/README.md
touch database/schemas/users.sql
touch database/schemas/diaries.sql
touch database/schemas/tags.sql
touch database/schemas/diary_tags.sql
touch database/schemas/weekly_diaries.sql
touch database/seeds/sample_tags.sql

# 문서 파일
touch docs/API_SPEC.md
touch docs/ARCHITECTURE.md
touch docs/SETUP_GUIDE.md
```

### Step 3: .gitignore 설정

루트 `.gitignore`:
```
# IDE
.idea/
*.iml
.vscode/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db

# 환경
.env
.env.local
.env.*.local

# 로그
*.log
logs/

# 빌드
build/
dist/
*.o
*.class

# 캐시
.cache/
__pycache__/
```

### Step 4: 루트 README.md 작성

```markdown
# 애기 다이어리 & AI 그림동화 앱

## 프로젝트 개요
아이의 일상을 3줄 다이어리 + 1장 사진으로 기록하고, AI(Vision, Gemini)를 통해 동화/감정/전문가 의견을 자동 생성하는 완전한 스택 애플리케이션

## 프로젝트 구조

```
android_programming_assignment/
├── android_app/         # Android 모바일 앱 (Java, API 24+)
├── backend/             # REST API 서버 (Node.js)
├── database/            # PostgreSQL 스크립트
└── docs/                # 프로젝트 문서
```

## 주요 기능

### 1. 데일리 다이어리
- 3줄 텍스트 + 1장 사진
- Vision API로 이미지 설명 추출
- Gemini API로 동화/감정/전문가 의견 생성

### 2. 주간 다이어리
- 7일 다이어리 종합
- AI 제목/요약 생성
- BGM 재생

### 3. 검색 & 필터
- 태그별 검색
- 연도/주차별 필터

### 4. 연속 작성 추적
- Best Streak / Current Streak

## 기술 스택

| 계층 | 기술 |
|------|------|
| **프론트엔드** | Android (Java), API 24+ |
| **백엔드** | Node.js Express |
| **데이터베이스** | PostgreSQL |
| **외부 API** | Google Vision, Gemini |

## 설치 및 실행

### 개발 환경 설정
자세한 내용은 [SETUP_GUIDE.md](docs/SETUP_GUIDE.md) 참고

### Android App
```bash
cd android_app
./gradlew build
./gradlew installDebug
```

### Backend
```bash
cd backend
npm install
npm run dev
```

### Database
```bash
psql -U postgres -d baby_diary -f database/init.sql
```

## 문서
- [API 명세](docs/API_SPEC.md)
- [데이터베이스 스키마](docs/DATABASE_SCHEMA.md)
- [전체 아키텍처](docs/ARCHITECTURE.md)
- [개발 가이드](docs/DEVELOPMENT_GUIDE.md)

## 개발자
kyowon1108

## 라이선스
MIT

## 연락처
이메일: kyowon1108@gmail.com
```

### Step 5: Git 커밋

```bash
# 초기 커밋
git add .
git commit -m "feat: Initialize project structure (android_app, backend, database)"

# 각 부분별 커밋
git add android_app/
git commit -m "feat: Add Android app structure and configuration"

git add backend/
git commit -m "feat: Add backend server (Node.js/Express)"

git add database/
git commit -m "feat: Add database schemas and initialization scripts"

git add docs/
git commit -m "docs: Add project documentation"

# 원격 저장소에 푸시
git push origin main
```

---

## 개발 환경 설정

### 전체 환경 설정 (SETUP_GUIDE.md 내용)

```markdown
# 개발 환경 설정 가이드

## 사전 요구사항

### 공통
- Git
- GitHub 계정

### Android 개발
- Android Studio 최신 버전
- JDK 11+
- Android SDK 34

### Backend 개발
- Node.js 14+
- npm

### Database 개발
- PostgreSQL 12+
- pgAdmin (선택)

## Step-by-Step 설정

### 1. 레포지토리 클론
```bash
git clone https://github.com/kyowon1108/android_programming_assignment.git
cd android_programming_assignment
```

### 2. Android 앱 설정
```bash
cd android_app
./gradlew build
```

### 3. Backend 설정
```bash
cd backend
npm install
cp .env.example .env
# .env 수정 (API 키, DB 설정)
npm run dev
```

### 4. Database 설정
```bash
cd database
createdb baby_diary
psql -U postgres -d baby_diary -f init.sql
```

## API 키 설정

### Google Cloud 설정
1. [Google Cloud Console](https://console.cloud.google.com) 방문
2. 프로젝트 생성
3. Vision API 활성화
4. Gemini API 활성화
5. API 키 생성 (.env에 저장)

## 테스트

### Android 테스트
```bash
cd android_app
./gradlew test
./gradlew connectedAndroidTest
```

### Backend 테스트
```bash
cd backend
npm test
```

## 주의사항
- .env 파일은 Git에 커밋하지 말 것 (.gitignore 확인)
- API 키는 안전하게 관리할 것
- 모든 변경사항은 새로운 브랜치에서 개발
```

---

## 주요 구성 파일

### 1. 최상위 .gitignore

```
# IDE
.idea/
.vscode/
*.iml
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# 환경
.env
.env.local

# 로그
*.log
logs/

# 캐시
.cache/
__pycache__/
node_modules/
venv/

# 빌드
build/
dist/
*.o

# Android
android_app/build/
android_app/.gradle/

# Backend
backend/node_modules/
backend/venv/
backend/uploads/

# 마이그레이션 (선택)
*.db
*.sql.bak
```

### 2. android_app/.gitignore

```
.gradle/
build/
*.apk
*.ap_
.idea/
*.iml
local.properties
*.log
.DS_Store
```

### 3. backend/.gitignore

```
node_modules/
npm-debug.log
.env
.env.local
venv/
__pycache__/
uploads/
.DS_Store
*.log
```

---

## 최종 폴더 구조 확인

생성 후 다음 명령으로 확인:

```bash
tree -L 3 -I 'node_modules|build|venv'

# 또는 (tree 없는 경우)
find . -type d -name "node_modules" -prune -o -type d -print | grep -E "^\./" | head -50
```

예상 출력:
```
android_programming_assignment/
├── .git/
├── .gitignore
├── README.md
├── LICENSE
│
├── android_app/
│   ├── .gitignore
│   ├── README.md
│   ├── build.gradle
│   ├── settings.gradle
│   └── app/
│       ├── build.gradle
│       └── src/
│
├── backend/
│   ├── .gitignore
│   ├── .env.example
│   ├── README.md
│   ├── package.json
│   ├── app.js
│   ├── config/
│   ├── routes/
│   ├── controllers/
│   ├── services/
│   ├── middleware/
│   └── utils/
│
├── database/
│   ├── README.md
│   ├── init.sql
│   ├── schemas/
│   ├── migrations/
│   └── seeds/
│
└── docs/
    ├── API_SPEC.md
    ├── DATABASE_SCHEMA.md
    ├── ARCHITECTURE.md
    └── SETUP_GUIDE.md
```

---

## 다음 단계

1. **android_app 폴더에서**
   - 기존 구조 유지
   - build.gradle 확인 및 필요시 업데이트

2. **backend 폴더에서**
   - npm install 실행
   - 환경변수 설정

3. **database 폴더에서**
   - PostgreSQL 서버 시작
   - init.sql 실행

4. **모든 폴더 커밋**
   - 단계별로 커밋
   - GitHub에 푸시

