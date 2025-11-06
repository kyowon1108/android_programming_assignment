"""
Diary Routes
다이어리 CRUD API 엔드포인트
"""

from fastapi import APIRouter, HTTPException, status, Depends, UploadFile, File, Form
from typing import Optional, List
from datetime import date as date_type
import logging
import json

from app.models.schemas import (
    DiaryResponse, DiaryListResponse, DiaryUpdate,
    TagResponse, SuccessResponse
)
from app.config.database import get_db
from app.utils.auth import get_current_user_id
from app.utils.file_handler import save_uploaded_file, delete_file, get_full_path
from app.utils.date_utils import get_week_number, calculate_streak
from app.services.vision_service import vision_service
from app.services.gemini_service import gemini_service

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/diaries", tags=["Diaries"])


@router.post("", response_model=DiaryResponse, status_code=status.HTTP_201_CREATED)
async def create_diary(
    date: str = Form(...),
    description: str = Form(...),
    tag_ids: Optional[str] = Form("[]"),
    photo: UploadFile = File(...),
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Create new diary entry

    - **date**: Diary date (YYYY-MM-DD format)
    - **description**: User's diary text (3-line description)
    - **tag_ids**: JSON array of tag IDs (optional)
    - **photo**: Image file (required)

    Requires: Bearer token in Authorization header
    """
    try:
        # Parse date
        diary_date = date_type.fromisoformat(date)

        # Parse tag_ids
        tag_id_list = json.loads(tag_ids) if tag_ids else []

        # Save photo
        photo_path = await save_uploaded_file(photo, subfolder="diaries")

        # Get week number
        year, week_number = get_week_number(diary_date)

        with db.get_cursor() as cursor:
            # Check for existing diary on same date
            cursor.execute(
                "SELECT diary_id FROM diaries WHERE user_id = %s AND date = %s",
                (user_id, diary_date)
            )
            if cursor.fetchone():
                delete_file(photo_path)
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Diary already exists for this date"
                )

            # Analyze image with Vision API
            full_photo_path = get_full_path(photo_path)
            vision_response = await vision_service.analyze_image(full_photo_path)
            vision_description = vision_service.generate_description(vision_response)

            # Generate content with Gemini API
            story = await gemini_service.generate_story(vision_description, description)
            emotion = await gemini_service.analyze_emotion(description)
            expert_comment = await gemini_service.generate_expert_comment(description, emotion)

            # Insert diary
            cursor.execute(
                """
                INSERT INTO diaries (
                    user_id, date, description, photo_url, vision_description,
                    generated_story, expert_comment, emotion, year, week_number
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                RETURNING diary_id, user_id, date, description, photo_url,
                          vision_description, generated_story, expert_comment,
                          emotion, year, week_number, created_at, updated_at
                """,
                (user_id, diary_date, description, photo_path, vision_description,
                 story, expert_comment, emotion, year, week_number)
            )

            diary_row = cursor.fetchone()

            # Insert tags
            if tag_id_list:
                for tag_id in tag_id_list:
                    cursor.execute(
                        "INSERT INTO diary_tags (diary_id, tag_id) VALUES (%s, %s) ON CONFLICT DO NOTHING",
                        (diary_row["diary_id"], tag_id)
                    )

            # Update user streak
            cursor.execute(
                "SELECT last_diary_date, current_streak, best_streak FROM users WHERE user_id = %s",
                (user_id,)
            )
            user_data = cursor.fetchone()

            new_streak = calculate_streak(
                user_data["last_diary_date"],
                diary_date,
                user_data["current_streak"]
            )
            new_best_streak = max(new_streak, user_data["best_streak"])

            cursor.execute(
                """
                UPDATE users
                SET last_diary_date = %s, current_streak = %s, best_streak = %s
                WHERE user_id = %s
                """,
                (diary_date, new_streak, new_best_streak, user_id)
            )

            # Get tags for response
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

            return DiaryResponse(**diary_dict)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Create diary error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to create diary"
        )


@router.get("", response_model=DiaryListResponse)
async def get_diaries(
    page: int = 1,
    page_size: int = 20,
    year: Optional[int] = None,
    week_number: Optional[int] = None,
    emotion: Optional[str] = None,
    tag_id: Optional[int] = None,
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Get user's diaries with pagination and filters

    - **page**: Page number (default: 1)
    - **page_size**: Items per page (default: 20, max: 100)
    - **year**: Filter by year (optional)
    - **week_number**: Filter by week number (optional)
    - **emotion**: Filter by emotion (optional)
    - **tag_id**: Filter by tag ID (optional)

    Requires: Bearer token in Authorization header
    """
    try:
        page_size = min(page_size, 100)
        offset = (page - 1) * page_size

        with db.get_cursor() as cursor:
            # Build query with filters
            query = """
                SELECT DISTINCT d.diary_id, d.user_id, d.date, d.description, d.photo_url,
                       d.vision_description, d.generated_story, d.expert_comment,
                       d.emotion, d.year, d.week_number, d.created_at, d.updated_at
                FROM diaries d
            """

            conditions = ["d.user_id = %s"]
            params = [user_id]

            if tag_id:
                query += " JOIN diary_tags dt ON d.diary_id = dt.diary_id"
                conditions.append("dt.tag_id = %s")
                params.append(tag_id)

            if year:
                conditions.append("d.year = %s")
                params.append(year)

            if week_number:
                conditions.append("d.week_number = %s")
                params.append(week_number)

            if emotion:
                conditions.append("d.emotion = %s")
                params.append(emotion)

            query += " WHERE " + " AND ".join(conditions)
            query += " ORDER BY d.date DESC LIMIT %s OFFSET %s"
            params.extend([page_size, offset])

            cursor.execute(query, params)
            diary_rows = cursor.fetchall()

            # Get total count
            count_query = f"""
                SELECT COUNT(DISTINCT d.diary_id)
                FROM diaries d
                {' JOIN diary_tags dt ON d.diary_id = dt.diary_id' if tag_id else ''}
                WHERE {' AND '.join(conditions)}
            """
            cursor.execute(count_query, params[:-2])  # Exclude LIMIT and OFFSET params
            total = cursor.fetchone()["count"]

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

            return DiaryListResponse(
                diaries=diaries,
                total=total,
                page=page,
                page_size=page_size
            )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Get diaries error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve diaries"
        )


@router.get("/{diary_id}", response_model=DiaryResponse)
async def get_diary(
    diary_id: int,
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Get specific diary by ID

    Requires: Bearer token in Authorization header
    """
    try:
        with db.get_cursor() as cursor:
            cursor.execute(
                """
                SELECT diary_id, user_id, date, description, photo_url,
                       vision_description, generated_story, expert_comment,
                       emotion, year, week_number, created_at, updated_at
                FROM diaries
                WHERE diary_id = %s AND user_id = %s
                """,
                (diary_id, user_id)
            )

            diary_row = cursor.fetchone()

            if not diary_row:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Diary not found"
                )

            # Get tags
            cursor.execute(
                """
                SELECT t.tag_id, t.tag_name, t.tag_category
                FROM tags t
                JOIN diary_tags dt ON t.tag_id = dt.tag_id
                WHERE dt.diary_id = %s
                """,
                (diary_id,)
            )
            tags = [TagResponse(**tag) for tag in cursor.fetchall()]

            diary_dict = dict(diary_row)
            diary_dict["tags"] = tags

            return DiaryResponse(**diary_dict)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Get diary error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve diary"
        )


@router.delete("/{diary_id}", response_model=SuccessResponse)
async def delete_diary(
    diary_id: int,
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Delete diary

    Requires: Bearer token in Authorization header
    """
    try:
        with db.get_cursor() as cursor:
            # Get diary to delete
            cursor.execute(
                "SELECT photo_url FROM diaries WHERE diary_id = %s AND user_id = %s",
                (diary_id, user_id)
            )
            diary = cursor.fetchone()

            if not diary:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Diary not found"
                )

            # Delete diary (cascade will delete tags)
            cursor.execute(
                "DELETE FROM diaries WHERE diary_id = %s AND user_id = %s",
                (diary_id, user_id)
            )

            # Delete photo file
            delete_file(diary["photo_url"])

            return SuccessResponse(message="Diary deleted successfully")

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Delete diary error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to delete diary"
        )
