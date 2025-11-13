# Google API 설정 가이드

## 🔧 현재 API 에러 상황

### Vision API
- **에러**: 403 Forbidden
- **원인**: API 키가 유효하지 않거나 Vision API가 활성화되지 않음

### Gemini API
- **에러**: 404 Not Found → **해결됨** ✅
- **원인**: 잘못된 모델명 사용 (gemini-pro → gemini-1.5-flash로 수정)

---

## 📋 API 키 설정 방법

### 1. Google Cloud Console 설정 (Vision API)

1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. 프로젝트 생성 또는 선택
3. **APIs & Services** → **Library** 이동
4. **Cloud Vision API** 검색 후 **Enable** 클릭
5. **APIs & Services** → **Credentials** 이동
6. **CREATE CREDENTIALS** → **API key** 선택
7. 생성된 API 키 복사

### 2. Google AI Studio 설정 (Gemini API)

1. [Google AI Studio](https://makersuite.google.com/app/apikey) 접속
2. **Get API key** 클릭
3. **Create API key in new project** 선택
4. 생성된 API 키 복사

### 3. Backend .env 파일 수정

```bash
# backend/.env 파일
GOOGLE_VISION_API_KEY=your_vision_api_key_here
GOOGLE_GEMINI_API_KEY=your_gemini_api_key_here
```

---

## 🚨 중요 사항

### Vision API 활성화 확인
```bash
# Vision API가 활성화되었는지 확인
curl -X POST \
  "https://vision.googleapis.com/v1/images:annotate?key=YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "requests": [{
      "image": {
        "content": "/9j/4AAQSkZJRgABAQAAAQ..."
      },
      "features": [{
        "type": "LABEL_DETECTION",
        "maxResults": 1
      }]
    }]
  }'
```

### Gemini API 테스트
```bash
# Gemini API 테스트
curl -X POST \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "contents": [{
      "parts": [{
        "text": "Hello"
      }]
    }]
  }'
```

---

## 💡 임시 해결 방법 (API 키 없이 테스트)

만약 API 키를 즉시 설정할 수 없다면, Mock 데이터를 사용하도록 설정:

### backend/app/config/settings.py
```python
# Mock 모드 활성화
USE_MOCK_AI = True  # True로 설정하면 API 호출 대신 Mock 데이터 사용
```

### Mock 데이터 사용 시 동작
- Vision API: "이미지에서 아기, 장난감, 미소가 감지되었습니다."
- Gemini Story: "오늘의 이야기: [사용자 입력 내용]"
- Emotion: "joy" (기본값)
- Expert Comment: "아이의 일상을 기록하는 것은 성장 과정을 이해하는데 도움이 됩니다."

---

## 📝 API 키 보안 주의사항

1. **절대 Git에 커밋하지 마세요**
   - `.env` 파일은 `.gitignore`에 포함되어 있어야 함
   - 실수로 커밋한 경우 즉시 API 키 재생성

2. **API 키 제한 설정**
   - Google Cloud Console에서 API 키에 제한 설정
   - IP 주소 제한 또는 HTTP referrer 제한 추가
   - 사용량 할당량 설정

3. **프로덕션 환경**
   - 환경 변수로 관리
   - Secret Manager 사용 권장

---

## 🔍 디버깅 팁

### Backend 로그 확인
```bash
# 상세 로그 모드로 실행
uvicorn app.main:app --reload --log-level debug
```

### 로그에서 확인할 내용
```
INFO: Analyzing image with Vision API: /path/to/image.jpg
INFO: Vision API success: 이미지에서 다음과 같은 요소들이 감지되었습니다...
INFO: Generating story with Gemini API
INFO: Gemini API success - Emotion: joy
```

### 에러 발생 시
```
ERROR: Vision API failed: 403 Forbidden
→ API 키 확인, Vision API 활성화 확인

ERROR: Gemini API failed: 404 Not Found
→ 모델명 확인 (gemini-1.5-flash 사용)
```

---

## ✅ 체크리스트

- [ ] Google Cloud 프로젝트 생성
- [ ] Vision API 활성화
- [ ] Vision API 키 생성
- [ ] Gemini API 키 생성 (Google AI Studio)
- [ ] .env 파일에 API 키 추가
- [ ] Backend 재시작
- [ ] API 테스트

---

## 🆘 문제 해결

### Q: Vision API 403 에러가 계속 발생합니다
A:
1. API 키가 올바른지 확인
2. Vision API가 활성화되었는지 확인
3. 결제 계정이 연결되었는지 확인 (무료 할당량 초과 시)

### Q: Gemini API가 작동하지 않습니다
A:
1. 모델명이 `gemini-1.5-flash`인지 확인
2. API 키가 Google AI Studio에서 생성된 것인지 확인
3. 지역 제한 확인 (일부 국가에서는 사용 불가)

### Q: 둘 다 설정하기 어렵습니다
A: Mock 모드를 사용하여 테스트 가능 (위의 "임시 해결 방법" 참조)