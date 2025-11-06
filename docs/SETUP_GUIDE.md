# Setup Guide

## Prerequisites

### System Requirements
- macOS, Linux, or Windows 10+
- 8GB RAM minimum (16GB recommended)
- 20GB free disk space

### Required Software
1. **Git** - Version control
2. **Android Studio** - Latest stable version
3. **Python 3.10+** - Backend runtime
4. **PostgreSQL 12+** - Database
5. **JDK 11+** - Java development

## Quick Start

```bash
# Clone repository
git clone https://github.com/kyowon1108/android_programming_assignment.git
cd android_programming_assignment

# Backend setup
cd backend
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env
# Edit .env with your API keys

# Database setup
createdb baby_diary
psql -U postgres -d baby_diary -f ../database/init.sql

# Start backend
python main.py

# In another terminal - Android app
cd ../android_app
./gradlew build
# Open in Android Studio and run
```

## Detailed Setup

### 1. Development Environment Setup

#### Install Android Studio
1. Download from https://developer.android.com/studio
2. Install Android SDK 34
3. Configure AVD (Android Virtual Device)
   - Recommended: Pixel 5, API 34
   - RAM: 2GB+
   - Internal Storage: 4GB+

#### Install Python
```bash
# macOS
brew install python@3.10

# Ubuntu/Debian
sudo apt-get update
sudo apt-get install python3.10 python3.10-venv python3-pip

# Windows
# Download from https://www.python.org/downloads/
```

#### Install PostgreSQL
```bash
# macOS
brew install postgresql
brew services start postgresql

# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql

# Windows
# Download from https://www.postgresql.org/download/windows/
```

### 2. Project Setup

#### Clone Repository
```bash
git clone https://github.com/kyowon1108/android_programming_assignment.git
cd android_programming_assignment
```

#### Backend Setup
```bash
cd backend

# Create virtual environment
python -m venv venv

# Activate virtual environment
# macOS/Linux
source venv/bin/activate
# Windows
venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Setup environment variables
cp .env.example .env
```

#### Configure .env File
Edit `backend/.env`:
```env
# Server Configuration
ENVIRONMENT=development
HOST=0.0.0.0
PORT=8000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=baby_diary
DB_USER=postgres  # or your username
DB_PASSWORD=      # your password

# JWT Authentication
JWT_SECRET=your_super_secret_jwt_key_here
JWT_ALGORITHM=HS256
JWT_EXPIRATION_HOURS=720

# Google API Keys (Get from Google Cloud Console)
GOOGLE_VISION_API_KEY=your_actual_vision_api_key
GOOGLE_GEMINI_API_KEY=your_actual_gemini_api_key

# File Upload Configuration
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=10485760
```

#### Database Setup
```bash
# Create database
createdb baby_diary

# Run initialization script
psql -U postgres -d baby_diary -f database/init.sql

# Verify tables
psql -U postgres -d baby_diary -c "\dt"
```

#### Start Backend Server
```bash
cd backend
python main.py

# Or use the convenience script
../run_server.sh
```

Server will run at http://localhost:8000

### 3. Android App Setup

#### Open in Android Studio
1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to `android_app` folder
4. Wait for Gradle sync

#### Configure API Endpoint
Edit `android_app/app/src/main/java/com/example/babydiary/util/Constants.java`:

```java
// For emulator
private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";

// For real device - Replace with your computer's IP
private static final String BASE_URL_DEVICE = "http://192.168.x.x:8000";
```

To find your computer's IP:
```bash
# macOS/Linux
ifconfig | grep "inet "

# Windows
ipconfig
```

#### Build and Run
```bash
cd android_app

# Build
./gradlew build

# Install on connected device/emulator
./gradlew installDebug

# Or use Android Studio's Run button
```

### 4. API Keys Setup

#### Google Cloud Vision API
1. Go to https://console.cloud.google.com/
2. Create a new project or select existing
3. Enable "Cloud Vision API"
4. Go to "APIs & Services" → "Credentials"
5. Create API Key
6. Add to `.env` file

#### Google Gemini API
1. Go to https://aistudio.google.com/apikey
2. Click "Create API Key"
3. Copy the key
4. Add to `.env` file

### 5. Testing

#### Backend Tests
```bash
cd backend
python -m pytest tests/
```

#### Android Tests
```bash
cd android_app
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

### 6. Common Issues & Solutions

#### Issue: PostgreSQL Connection Error
```
psycopg2.OperationalError: FATAL: role "postgres" does not exist
```
**Solution:**
```bash
# macOS
createuser -s postgres

# Or use your system username
DB_USER=your_username  # in .env
```

#### Issue: Android Build Error - SDK Not Found
```
SDK location not found
```
**Solution:**
Create `local.properties` in `android_app`:
```
sdk.dir=/path/to/Android/Sdk
```

#### Issue: Cleartext Traffic Error
```
Cleartext HTTP traffic not permitted
```
**Solution:**
Already configured in `network_security_config.xml`. For production, use HTTPS.

#### Issue: Permission Denied on Android
```
Permission denied for camera/storage
```
**Solution:**
Grant permissions in Settings → Apps → Baby Diary → Permissions

#### Issue: Backend Import Error
```
ModuleNotFoundError: No module named 'xxx'
```
**Solution:**
```bash
pip install -r requirements.txt
```

### 7. Development Tools

#### Recommended VS Code Extensions
- Python
- Pylance
- PostgreSQL
- Markdown All in One

#### Recommended Android Studio Plugins
- Markdown support
- JSON viewer
- ADB Idea

#### Database Management
- pgAdmin 4
- DBeaver
- TablePlus (macOS)

### 8. Deployment

#### Backend Deployment (Example with Heroku)
```bash
# Install Heroku CLI
brew install heroku/brew/heroku

# Login
heroku login

# Create app
heroku create baby-diary-backend

# Add PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Deploy
git push heroku main

# Set environment variables
heroku config:set GOOGLE_VISION_API_KEY=xxx
heroku config:set GOOGLE_GEMINI_API_KEY=xxx
```

#### Android App Distribution
1. Generate signed APK in Android Studio
2. Upload to Google Play Console
3. Or distribute via Firebase App Distribution

### 9. Monitoring

#### Backend Logs
```bash
# Development
tail -f logs/app.log

# Production (Heroku)
heroku logs --tail
```

#### Android Logs
```bash
adb logcat | grep "BabyDiary"
```

### 10. Backup & Recovery

#### Database Backup
```bash
# Backup
pg_dump -U postgres baby_diary > backup_$(date +%Y%m%d).sql

# Restore
psql -U postgres baby_diary < backup_20250107.sql
```

#### Code Backup
```bash
git add .
git commit -m "Backup: $(date +%Y-%m-%d)"
git push origin main
```

## Support

For issues or questions:
- GitHub Issues: https://github.com/kyowon1108/android_programming_assignment/issues
- Email: kyowon1108@gmail.com

## License

MIT License - See LICENSE file for details