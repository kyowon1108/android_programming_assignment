# 감정 "neutral" 생성 원리 설명

## 1. 감정 분석 시스템 구조

### Gemini API 감정 분석 프롬프트
```python
async def analyze_emotion(self, diary_description: str) -> str:
    prompt = f"""다음 아이 일기의 전반적인 감정을 분석하세요.
반드시 다음 중 하나로만 답변하세요: joy, sadness, anger, surprise, fear, neutral

일기: {diary_description}

감정:"""
```

### 감정 카테고리
- **joy (기쁨)**: 웃음, 행복, 즐거움, 신나함과 관련된 내용
- **sadness (슬픔)**: 울음, 아쉬움, 그리움과 관련된 내용
- **anger (화남)**: 짜증, 분노, 투정과 관련된 내용
- **surprise (놀람)**: 놀라움, 신기함, 새로운 발견과 관련된 내용
- **fear (두려움)**: 무서움, 걱정, 불안과 관련된 내용
- **neutral (중립)**: 명확한 감정이 없는 일상적인 기록

## 2. "neutral" 감정이 생성되는 경우

### 2.1. AI가 명확한 감정을 판단하지 못한 경우
```
예시 일기: "오늘 아기가 밥을 먹었습니다."
→ 긍정/부정 감정이 명확하지 않음
→ Gemini 응답: "neutral"
```

### 2.2. 여러 감정이 혼재된 경우
```
예시 일기: "아기가 처음에는 울었지만, 나중에 웃었어요."
→ 슬픔 + 기쁨이 혼재
→ 전체적인 감정이 애매함
→ Gemini 응답: "neutral"
```

### 2.3. 감정 표현이 없는 사실적 기록
```
예시 일기: "오늘 아기 몸무게는 7.5kg입니다."
→ 감정 없는 객관적 사실 기록
→ Gemini 응답: "neutral"
```

### 2.4. API 오류 발생 시 기본값
```python
try:
    emotion = await gemini_service.analyze_emotion(description)
except Exception as e:
    emotion = "neutral"  # 에러 시 기본값
```

## 3. 현재 구현 코드

### backend/app/services/gemini_service.py
```python
async def analyze_emotion(self, diary_description: str) -> str:
    """일기 내용에서 주요 감정 추출"""

    prompt = f"""다음 아이 일기의 전반적인 감정을 분석하세요.
반드시 다음 중 하나로만 답변하세요: joy, sadness, anger, surprise, fear, neutral

일기: {diary_description}

감정:"""

    try:
        # Gemini API 호출
        response = self.model.generate_content(prompt)
        emotion_text = response.text.strip().lower()

        # 유효성 검증
        valid_emotions = ["joy", "sadness", "anger", "surprise", "fear", "neutral"]

        for emotion in valid_emotions:
            if emotion in emotion_text:
                logger.info(f"Emotion detected: {emotion}")
                return emotion

        # 감정을 찾을 수 없으면 기본값
        logger.warning(f"No valid emotion found, defaulting to 'neutral'")
        return "neutral"

    except Exception as e:
        logger.error(f"Emotion analysis failed: {str(e)}")
        return "neutral"  # 에러 발생 시 기본값
```

## 4. "neutral" 감정의 의미와 활용

### 의미
- **중립적 감정**: 특별히 강한 감정이 없는 평범한 일상
- **균형잡힌 상태**: 여러 감정이 균형을 이루는 상태
- **객관적 기록**: 감정보다는 사실 위주의 기록

### 활용
1. **성장 기록**: 아이의 신체적 성장 기록 (키, 몸무게 등)
2. **일상 기록**: 식사, 수면 등 일상적인 활동
3. **관찰 기록**: 부모의 객관적인 관찰 내용

## 5. 개선 방안 (선택사항)

### 더 정확한 감정 분석을 위한 프롬프트 개선
```python
async def analyze_emotion_improved(self, diary_description: str) -> str:
    prompt = f"""다음 아이 일기에서 가장 주요한 감정을 하나만 선택하세요.
부모가 느낀 감정과 아이가 표현한 감정을 종합적으로 고려하세요.

감정 카테고리와 키워드:
- joy (기쁨): 웃음, 행복, 즐거움, 신나함, 미소, 기뻐함
- sadness (슬픔): 울음, 아쉬움, 그리움, 서운함, 눈물
- anger (화남): 짜증, 분노, 투정, 화남, 불만
- surprise (놀람): 놀라움, 신기함, 발견, 깜짝, 처음
- fear (두려움): 무서움, 걱정, 불안, 두려움, 무서워함
- neutral (중립): 평범한 일상, 사실 기록, 명확한 감정 없음

일기: {diary_description}

분석 과정:
1. 일기에서 감정 키워드를 찾으세요
2. 전체적인 맥락을 고려하세요
3. 가장 적합한 감정 하나를 선택하세요

답변 형식: 감정 키워드만 출력 (예: joy)
감정:"""

    # ... 나머지 코드 동일
```

### 감정 신뢰도 추가
```python
def analyze_emotion_with_confidence(self, description: str) -> tuple[str, float]:
    """
    Returns:
        (emotion, confidence_score)
        confidence_score: 0.0 ~ 1.0
    """
    # AI 응답에서 신뢰도도 함께 요청
    # 신뢰도가 낮으면 neutral로 분류
```

## 6. 통계 및 분석

### 감정 분포 예상
- joy: 40% (아이의 긍정적 순간이 많이 기록됨)
- neutral: 25% (일상적 기록)
- surprise: 15% (새로운 발견과 성장)
- sadness: 10% (울음, 투정)
- anger: 7% (짜증, 불만)
- fear: 3% (두려움은 상대적으로 적음)

## 7. 결론

"neutral" 감정은:
1. **정상적인 분류**: 모든 일기가 강한 감정을 담지는 않음
2. **안전한 기본값**: 불확실한 경우 중립으로 분류
3. **객관적 기록**: 감정보다 사실 위주의 기록에 적합

이는 AI 시스템이 과도한 해석을 피하고, 명확하지 않은 경우 중립적 입장을 취하는 보수적이고 안전한 접근 방식입니다.