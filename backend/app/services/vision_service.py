"""
Google Vision API Service
이미지 분석 및 텍스트/라벨 추출
"""

import httpx
import base64
import logging
from typing import Optional

from app.config.settings import settings

logger = logging.getLogger(__name__)


class VisionService:
    """Google Cloud Vision API integration"""

    def __init__(self):
        self.api_key = settings.GOOGLE_VISION_API_KEY
        self.base_url = "https://vision.googleapis.com/v1/images:annotate"

    async def analyze_image(self, image_path: str) -> dict:
        """
        Analyze image using Vision API

        Args:
            image_path: Path to image file

        Returns:
            Dictionary containing Vision API results
        """
        try:
            # Read and encode image
            with open(image_path, "rb") as image_file:
                image_content = base64.b64encode(image_file.read()).decode("utf-8")

            # Prepare request
            request_body = {
                "requests": [
                    {
                        "image": {"content": image_content},
                        "features": [
                            {"type": "LABEL_DETECTION", "maxResults": 10},
                            {"type": "TEXT_DETECTION"},
                            {"type": "SAFE_SEARCH_DETECTION"}
                        ]
                    }
                ]
            }

            # Call Vision API
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.base_url}?key={self.api_key}",
                    json=request_body,
                    timeout=30.0
                )
                response.raise_for_status()
                result = response.json()

            return result

        except Exception as e:
            logger.error(f"Vision API error: {e}")
            raise

    def extract_labels(self, vision_response: dict) -> str:
        """
        Extract labels from Vision API response

        Args:
            vision_response: Response from Vision API

        Returns:
            Comma-separated labels
        """
        try:
            labels = vision_response["responses"][0].get("labelAnnotations", [])
            label_descriptions = [label["description"] for label in labels[:5]]
            return ", ".join(label_descriptions) if label_descriptions else "No labels detected"
        except Exception as e:
            logger.error(f"Label extraction error: {e}")
            return "Label extraction failed"

    def extract_text(self, vision_response: dict) -> str:
        """
        Extract text from Vision API response

        Args:
            vision_response: Response from Vision API

        Returns:
            Extracted text
        """
        try:
            text_annotations = vision_response["responses"][0].get("textAnnotations", [])
            if text_annotations:
                return text_annotations[0].get("description", "")
            return ""
        except Exception as e:
            logger.error(f"Text extraction error: {e}")
            return ""

    def generate_description(self, vision_response: dict) -> str:
        """
        Generate comprehensive description from Vision API response

        Args:
            vision_response: Response from Vision API

        Returns:
            Human-readable description
        """
        labels = self.extract_labels(vision_response)
        text = self.extract_text(vision_response)

        description_parts = []

        if labels and labels != "No labels detected":
            description_parts.append(f"이미지에서 다음과 같은 요소들이 감지되었습니다: {labels}.")

        if text:
            description_parts.append(f"이미지에 포함된 텍스트: {text}")

        if not description_parts:
            return "이미지를 분석했습니다."

        return " ".join(description_parts)


# Global instance
vision_service = VisionService()
