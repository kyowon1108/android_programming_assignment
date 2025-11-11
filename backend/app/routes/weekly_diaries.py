"""
Weekly Diary Routes
주간 다이어리 API 엔드포인트
"""

from fastapi import APIRouter, HTTPException, status, Depends, UploadFile, File, Form
from typing import Optional, List
import logging
import os
import uuid
from pathlib import Path

from app.models.schemas import (
    WeeklyDiaryCreate, WeeklyDiaryResponse,
    WeeklyDiaryFullResponse, DiaryResponse, TagResponse
)
from app.config.database import get_db
from app.utils.auth import get_current_user_id
from app.utils.date_utils import get_week_dates
from app.services.gemini_service import gemini_service

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/weekly_diaries", tags=["Weekly Diaries"])

# Upload directory
UPLOAD_DIR = Path("uploads/weekly")
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)


@router.post("", response_model=WeeklyDiaryResponse, status_code=status.HTTP_201_CREATED)
async def create_weekly_diary(
    year: int = Form(...),
    week_number: int = Form(...),
    photo: Optional[UploadFile] = File(None),
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Create or regenerate weekly diary with optional custom image

    - **year**: Year (2000-2100)
    - **week_number**: ISO week number (1-53)
    - **photo**: Optional custom image for weekly diary

    Aggregates all diaries from the specified week and generates AI summary

    Requires: Bearer token in Authorization header
    """
    try:
        # Validate input
        if year < 2000 or year > 2100:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid year (must be 2000-2100)"
            )
        if week_number < 1 or week_number > 53:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid week number (must be 1-53)"
            )

        start_date, end_date = get_week_dates(year, week_number)

        # Handle photo upload
        photo_filename = None
        user_uploaded_image = False

        if photo:
            # Validate file type
            if not photo.content_type or not photo.content_type.startswith("image/"):
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Invalid file type. Only images are allowed."
                )

            # Generate unique filename
            file_ext = os.path.splitext(photo.filename)[1] if photo.filename else ".jpg"
            photo_filename = f"weekly_{user_id}_{year}W{week_number:02d}_{uuid.uuid4().hex[:8]}{file_ext}"
            photo_path = UPLOAD_DIR / photo_filename

            # Save file
            with open(photo_path, "wb") as f:
                content = await photo.read()
                f.write(content)

            user_uploaded_image = True
            logger.info(f"Uploaded weekly diary image: {photo_filename}")

        with db.get_cursor() as cursor:
            # Get all diaries for the week
            cursor.execute(
                """
                SELECT description, generated_story
                FROM diaries
                WHERE user_id = %s AND year = %s AND week_number = %s
                ORDER BY date ASC
                """,
                (user_id, year, week_number)
            )
            weekly_diaries = cursor.fetchall()

            if not weekly_diaries:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="No diaries found for this week"
                )

            # Combine diary texts
            combined_text = "\n\n".join([
                f"일기 {idx + 1}:\n{diary['description']}\n동화: {diary['generated_story']}"
                for idx, diary in enumerate(weekly_diaries)
            ])

            # Generate weekly summary
            summary_data = await gemini_service.generate_weekly_summary(combined_text)

            # Insert or update weekly diary
            if photo_filename:
                # With custom image
                cursor.execute(
                    """
                    INSERT INTO weekly_diaries (
                        user_id, year, week_number, start_date, end_date,
                        weekly_summary_text, weekly_title, weekly_image_url, user_uploaded_image
                    )
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (user_id, year, week_number)
                    DO UPDATE SET
                        weekly_summary_text = EXCLUDED.weekly_summary_text,
                        weekly_title = EXCLUDED.weekly_title,
                        weekly_image_url = EXCLUDED.weekly_image_url,
                        user_uploaded_image = EXCLUDED.user_uploaded_image,
                        updated_at = CURRENT_TIMESTAMP
                    RETURNING week_id, user_id, year, week_number, start_date, end_date,
                              weekly_summary_text, weekly_image_url, weekly_title,
                              user_uploaded_image, created_at, updated_at
                    """,
                    (user_id, year, week_number, start_date, end_date,
                     summary_data["summary"], summary_data["title"], photo_filename, user_uploaded_image)
                )
            else:
                # Without custom image (AI generated or none)
                cursor.execute(
                    """
                    INSERT INTO weekly_diaries (
                        user_id, year, week_number, start_date, end_date,
                        weekly_summary_text, weekly_title
                    )
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (user_id, year, week_number)
                    DO UPDATE SET
                        weekly_summary_text = EXCLUDED.weekly_summary_text,
                        weekly_title = EXCLUDED.weekly_title,
                        updated_at = CURRENT_TIMESTAMP
                    RETURNING week_id, user_id, year, week_number, start_date, end_date,
                              weekly_summary_text, weekly_image_url, weekly_title,
                              user_uploaded_image, created_at, updated_at
                    """,
                    (user_id, year, week_number, start_date, end_date,
                     summary_data["summary"], summary_data["title"])
                )

            weekly_row = cursor.fetchone()

            return WeeklyDiaryResponse(**weekly_row)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Create weekly diary error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to create weekly diary"
        )


@router.get("", response_model=List[WeeklyDiaryResponse])
async def get_weekly_diaries(
    year: Optional[int] = None,
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Get user's weekly diaries

    - **year**: Filter by year (optional)

    Requires: Bearer token in Authorization header
    """
    try:
        with db.get_cursor() as cursor:
            if year:
                cursor.execute(
                    """
                    SELECT week_id, user_id, year, week_number, start_date, end_date,
                           weekly_summary_text, weekly_image_url, weekly_title,
                           user_uploaded_image, created_at, updated_at
                    FROM weekly_diaries
                    WHERE user_id = %s AND year = %s
                    ORDER BY year DESC, week_number DESC
                    """,
                    (user_id, year)
                )
            else:
                cursor.execute(
                    """
                    SELECT week_id, user_id, year, week_number, start_date, end_date,
                           weekly_summary_text, weekly_image_url, weekly_title,
                           user_uploaded_image, created_at, updated_at
                    FROM weekly_diaries
                    WHERE user_id = %s
                    ORDER BY year DESC, week_number DESC
                    """,
                    (user_id,)
                )

            weekly_rows = cursor.fetchall()
            return [WeeklyDiaryResponse(**row) for row in weekly_rows]

    except Exception as e:
        logger.error(f"Get weekly diaries error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve weekly diaries"
        )


@router.get("/{week_id}", response_model=WeeklyDiaryFullResponse)
async def get_weekly_diary_full(
    week_id: int,
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Get weekly diary with all daily diaries

    Requires: Bearer token in Authorization header
    """
    try:
        with db.get_cursor() as cursor:
            # Get weekly diary
            cursor.execute(
                """
                SELECT week_id, user_id, year, week_number, start_date, end_date,
                       weekly_summary_text, weekly_image_url, weekly_title,
                       user_uploaded_image, created_at, updated_at
                FROM weekly_diaries
                WHERE week_id = %s AND user_id = %s
                """,
                (week_id, user_id)
            )

            weekly_row = cursor.fetchone()

            if not weekly_row:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Weekly diary not found"
                )

            # Get daily diaries for this week
            cursor.execute(
                """
                SELECT diary_id, user_id, date, description, photo_url,
                       vision_description, generated_story, expert_comment,
                       emotion, year, week_number, created_at, updated_at
                FROM diaries
                WHERE user_id = %s AND year = %s AND week_number = %s
                ORDER BY date ASC
                """,
                (user_id, weekly_row["year"], weekly_row["week_number"])
            )

            diary_rows = cursor.fetchall()

            # Get tags for each diary
            diaries = []
            for diary_row in diary_rows:
                cursor.execute(
                    """
                    SELECT t.tag_id, t.tag_name, t.tag_category
                    FROM tags t
                    JOIN diary_tags dt ON t.tag_id = dt.tag_id
                    WHERE dt.diary_id = %s
                    """,
                    (diary_row["diary_id"],)
                )
                tags = [TagResponse(**tag) for tag in cursor.fetchall()]

                diary_dict = dict(diary_row)
                diary_dict["tags"] = tags
                diaries.append(DiaryResponse(**diary_dict))

            weekly_dict = dict(weekly_row)
            weekly_dict["diaries"] = diaries

            return WeeklyDiaryFullResponse(**weekly_dict)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Get weekly diary full error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve weekly diary"
        )


@router.get("/by-date/{year}/{week_number}", response_model=WeeklyDiaryFullResponse)
async def get_weekly_diary_by_date(
    year: int,
    week_number: int,
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Get weekly diary by year and week number

    Requires: Bearer token in Authorization header
    """
    try:
        with db.get_cursor() as cursor:
            # Get weekly diary
            cursor.execute(
                """
                SELECT week_id, user_id, year, week_number, start_date, end_date,
                       weekly_summary_text, weekly_image_url, weekly_title,
                       user_uploaded_image, created_at, updated_at
                FROM weekly_diaries
                WHERE user_id = %s AND year = %s AND week_number = %s
                """,
                (user_id, year, week_number)
            )

            weekly_row = cursor.fetchone()

            if not weekly_row:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Weekly diary not found"
                )

            # Get daily diaries
            cursor.execute(
                """
                SELECT diary_id, user_id, date, description, photo_url,
                       vision_description, generated_story, expert_comment,
                       emotion, year, week_number, created_at, updated_at
                FROM diaries
                WHERE user_id = %s AND year = %s AND week_number = %s
                ORDER BY date ASC
                """,
                (user_id, year, week_number)
            )

            diary_rows = cursor.fetchall()

            # Get tags for each diary
            diaries = []
            for diary_row in diary_rows:
                cursor.execute(
                    """
                    SELECT t.tag_id, t.tag_name, t.tag_category
                    FROM tags t
                    JOIN diary_tags dt ON t.tag_id = dt.tag_id
                    WHERE dt.diary_id = %s
                    """,
                    (diary_row["diary_id"],)
                )
                tags = [TagResponse(**tag) for tag in cursor.fetchall()]

                diary_dict = dict(diary_row)
                diary_dict["tags"] = tags
                diaries.append(DiaryResponse(**diary_dict))

            weekly_dict = dict(weekly_row)
            weekly_dict["diaries"] = diaries

            return WeeklyDiaryFullResponse(**weekly_dict)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Get weekly diary by date error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve weekly diary"
        )
