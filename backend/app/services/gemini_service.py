"""
Google Gemini API Service
AI 텍스트 생성 (동화, 감정 분석, 전문가 의견)
"""

import httpx
import logging
from typing import Optional

from app.config.settings import settings

logger = logging.getLogger(__name__)


class GeminiService:
    """Google Gemini API integration"""

    def __init__(self):
        self.api_key = settings.GOOGLE_GEMINI_API_KEY
        # Updated to use the correct Gemini model endpoint
        self.base_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    async def _call_gemini(self, prompt: str) -> str:
        """
        Call Gemini API with prompt

        Args:
            prompt: Text prompt for generation

        Returns:
            Generated text response

        Raises:
            Exception: If API call fails
        """
        try:
            request_body = {
                "contents": [
                    {
                        "parts": [
                            {"text": prompt}
                        ]
                    }
                ]
            }

            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.base_url}?key={self.api_key}",
                    json=request_body,
                    timeout=60.0
                )
                response.raise_for_status()
                result = response.json()

            # Extract text from response
            text = result["candidates"][0]["content"]["parts"][0]["text"]
            return text.strip()

        except Exception as e:
            logger.error(f"Gemini API error: {e}")
            raise

    async def generate_story(self, vision_description: str, diary_description: str) -> str:
        """
        Generate children's story from diary content

        Args:
            vision_description: Description from Vision API
            diary_description: User's diary text

        Returns:
            Generated story text
        """
        prompt = f"""다음은 아이의 사진 설명과 일기 내용입니다.
이를 바탕으로 5~7세 아이를 위한 짧은 그림동화를 500자 이내로 만들어주세요.
동화는 따뜻하고 긍정적이며, 아이의 경험을 재미있게 표현해야 합니다.

사진 설명: {vision_description}

일기 내용: {diary_description}

그림동화:"""

        try:
            story = await self._call_gemini(prompt)
            return story
        except Exception as e:
            logger.error(f"Story generation failed: {e}")
            return "동화 생성에 실패했습니다."

    async def analyze_emotion(self, diary_description: str) -> str:
        """
        Analyze emotion from diary text

        Args:
            diary_description: User's diary text

        Returns:
            Emotion label (joy, sadness, anger, surprise, fear, neutral)
        """
        prompt = f"""다음 아이 일기의 전반적인 감정을 분석하세요.
반드시 다음 중 하나로만 답변하세요: joy, sadness, anger, surprise, fear, neutral

일기: {diary_description}

감정:"""

        try:
            emotion = await self._call_gemini(prompt)
            # Clean up response and validate
            emotion = emotion.lower().strip()

            valid_emotions = ["joy", "sadness", "anger", "surprise", "fear", "neutral"]
            if emotion in valid_emotions:
                return emotion
            else:
                # Try to find valid emotion in response
                for valid_emotion in valid_emotions:
                    if valid_emotion in emotion:
                        return valid_emotion
                return "neutral"

        except Exception as e:
            logger.error(f"Emotion analysis failed: {e}")
            return "neutral"

    async def generate_expert_comment(self, diary_description: str, emotion: str) -> str:
        """
        Generate expert parenting comment

        Args:
            diary_description: User's diary text
            emotion: Detected emotion

        Returns:
            Expert comment text
        """
        # Map emotion to Korean
        emotion_map = {
            "joy": "기쁨",
            "sadness": "슬픔",
            "anger": "화남",
            "surprise": "놀람",
            "fear": "두려움",
            "neutral": "평온"
        }
        emotion_kr = emotion_map.get(emotion, "평온")

        prompt = f"""아이 일기를 읽은 육아전문가로서 부모에게 따뜻한 칭찬과 구체적인 조언을 300자 이내로 작성해주세요.
아이의 감정을 존중하고, 부모의 양육을 격려하며, 실천 가능한 팁을 제공해주세요.

감정: {emotion_kr}

일기: {diary_description}

전문가 의견:"""

        try:
            comment = await self._call_gemini(prompt)
            return comment
        except Exception as e:
            logger.error(f"Expert comment generation failed: {e}")
            return "전문가 의견 생성에 실패했습니다."

    async def generate_weekly_summary(self, diaries_text: str) -> dict:
        """
        Generate weekly summary title and text

        Args:
            diaries_text: Combined text from all weekly diaries

        Returns:
            Dictionary with 'title' and 'summary'
        """
        prompt = f"""다음은 한 주 동안의 아이 일기들입니다.
이 주의 일기들을 읽고, 주간 제목(20자 이내)과 주간 요약(300자 이내)을 작성해주세요.

응답 형식:
제목: [주간 제목]
요약: [주간 요약]

일주일 일기:
{diaries_text}

주간 다이어리:"""

        try:
            response = await self._call_gemini(prompt)

            # Parse response
            lines = response.split("\n")
            title = ""
            summary = ""

            for line in lines:
                if line.startswith("제목:"):
                    title = line.replace("제목:", "").strip()
                elif line.startswith("요약:"):
                    summary = line.replace("요약:", "").strip()

            # If parsing failed, use the whole response as summary
            if not title and not summary:
                title = "이번 주의 기록"
                summary = response

            return {
                "title": title[:200] if title else "이번 주의 기록",
                "summary": summary[:500] if summary else response[:500]
            }

        except Exception as e:
            logger.error(f"Weekly summary generation failed: {e}")
            return {
                "title": "이번 주의 기록",
                "summary": "주간 요약 생성에 실패했습니다."
            }


# Global instance
gemini_service = GeminiService()
