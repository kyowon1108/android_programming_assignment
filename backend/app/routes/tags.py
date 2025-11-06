"""
Tag Routes
태그 관리 API 엔드포인트
"""

from fastapi import APIRouter, HTTPException, status, Depends
from typing import List
import logging

from app.models.schemas import TagResponse
from app.config.database import get_db
from app.utils.auth import get_current_user_id

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/tags", tags=["Tags"])


@router.get("", response_model=List[TagResponse])
async def get_all_tags(db=Depends(get_db)):
    """
    Get all available tags

    No authentication required
    """
    try:
        with db.get_cursor() as cursor:
            cursor.execute(
                """
                SELECT tag_id, tag_name, tag_category
                FROM tags
                ORDER BY tag_category, tag_name
                """
            )
            tags = cursor.fetchall()
            return [TagResponse(**tag) for tag in tags]

    except Exception as e:
        logger.error(f"Get tags error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve tags"
        )


@router.get("/categories", response_model=List[str])
async def get_tag_categories(db=Depends(get_db)):
    """
    Get all tag categories

    No authentication required
    """
    try:
        with db.get_cursor() as cursor:
            cursor.execute(
                """
                SELECT DISTINCT tag_category
                FROM tags
                WHERE tag_category IS NOT NULL
                ORDER BY tag_category
                """
            )
            categories = [row["tag_category"] for row in cursor.fetchall()]
            return categories

    except Exception as e:
        logger.error(f"Get tag categories error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve tag categories"
        )


@router.get("/category/{category}", response_model=List[TagResponse])
async def get_tags_by_category(category: str, db=Depends(get_db)):
    """
    Get tags by category

    No authentication required
    """
    try:
        with db.get_cursor() as cursor:
            cursor.execute(
                """
                SELECT tag_id, tag_name, tag_category
                FROM tags
                WHERE tag_category = %s
                ORDER BY tag_name
                """,
                (category,)
            )
            tags = cursor.fetchall()
            return [TagResponse(**tag) for tag in tags]

    except Exception as e:
        logger.error(f"Get tags by category error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve tags by category"
        )
