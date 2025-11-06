#!/bin/bash

echo "=========================================="
echo "Baby Diary 백엔드 서버 초기 세팅"
echo "=========================================="
echo ""

# 1. PostgreSQL 데이터베이스 생성
echo "1. PostgreSQL 데이터베이스 생성 중..."
createdb baby_diary 2>/dev/null || echo "  (데이터베이스가 이미 존재하거나 PostgreSQL이 실행 중이 아닙니다)"
echo ""

# 2. 데이터베이스 스키마 초기화
echo "2. 데이터베이스 스키마 초기화 중..."
psql -d baby_diary -f database/init.sql
echo ""

# 3. Python 가상환경 생성 (있으면 스킵)
echo "3. Python 가상환경 확인/생성 중..."
cd backend
if [ ! -d "venv" ]; then
    python3 -m venv venv
    echo "  가상환경이 생성되었습니다."
else
    echo "  가상환경이 이미 존재합니다."
fi
echo ""

# 4. 가상환경 활성화 및 패키지 설치
echo "4. Python 패키지 설치 중..."
source venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
echo ""

# 5. .env 파일 생성 (없으면)
echo "5. 환경 변수 파일(.env) 확인 중..."
if [ ! -f ".env" ]; then
    cp .env.example .env
    echo "  .env 파일이 생성되었습니다."
    echo "  !!! 중요: backend/.env 파일을 열어 다음 값들을 설정해주세요:"
    echo "      - DB_PASSWORD (PostgreSQL 비밀번호)"
    echo "      - GOOGLE_VISION_API_KEY"
    echo "      - GOOGLE_GEMINI_API_KEY"
else
    echo "  .env 파일이 이미 존재합니다."
fi
echo ""

# 6. uploads 디렉토리 권한 확인
echo "6. 업로드 디렉토리 권한 확인 중..."
chmod 755 uploads
echo "  uploads/ 디렉토리 권한이 설정되었습니다."
echo ""

echo "=========================================="
echo "초기 세팅 완료!"
echo "=========================================="
echo ""
echo "서버 실행 방법:"
echo "  cd backend"
echo "  source venv/bin/activate"
echo "  python main.py"
echo ""
echo "또는 간단하게:"
echo "  ./run_server.sh"
echo ""
