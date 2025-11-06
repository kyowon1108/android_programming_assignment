#!/bin/bash

echo "=========================================="
echo "Baby Diary 백엔드 서버 실행"
echo "=========================================="
echo ""

cd backend

# 가상환경 활성화
if [ ! -d "venv" ]; then
    echo "Error: 가상환경이 없습니다. 먼저 ./backend_setup.sh를 실행해주세요."
    exit 1
fi

source venv/bin/activate

# .env 파일 확인
if [ ! -f ".env" ]; then
    echo "Error: .env 파일이 없습니다."
    echo "backend/.env.example을 복사하여 .env 파일을 만들고 설정해주세요."
    exit 1
fi

# 서버 실행
echo "서버를 시작합니다..."
echo "URL: http://localhost:8000"
echo "API 문서: http://localhost:8000/docs"
echo ""
echo "중지하려면 Ctrl+C를 누르세요."
echo ""

python main.py
