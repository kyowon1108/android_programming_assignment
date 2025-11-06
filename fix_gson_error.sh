#!/bin/bash

echo "=========================================="
echo "Gson Import 에러 자동 해결 스크립트"
echo "=========================================="
echo ""

cd android_app

echo "1. Gradle 캐시 정리 중..."
rm -rf .gradle
rm -rf app/build
rm -rf build
echo "   완료"
echo ""

echo "2. Gradle Clean 실행 중..."
./gradlew clean
echo "   완료"
echo ""

echo "3. Gradle 의존성 다운로드 중..."
./gradlew app:dependencies --refresh-dependencies
echo "   완료"
echo ""

echo "4. 프로젝트 빌드 중..."
./gradlew build
echo "   완료"
echo ""

echo "=========================================="
echo "해결 완료!"
echo "=========================================="
echo ""
echo "다음 단계:"
echo "1. Android Studio를 다시 시작하세요"
echo "2. File > Sync Project with Gradle Files 실행"
echo "3. 여전히 에러가 발생하면:"
echo "   File > Invalidate Caches / Restart... 실행"
echo ""
