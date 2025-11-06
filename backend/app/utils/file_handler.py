"""
File Upload Handler
이미지 파일 업로드 및 저장 처리
"""

import os
import uuid
import shutil
from fastapi import UploadFile, HTTPException, status
from PIL import Image
import logging

from app.config.settings import settings

logger = logging.getLogger(__name__)

ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"}
MAX_FILE_SIZE = settings.MAX_FILE_SIZE


def validate_image_file(file: UploadFile) -> None:
    """
    Validate uploaded image file

    Args:
        file: Uploaded file

    Raises:
        HTTPException: If file is invalid
    """
    # Check file extension
    file_ext = os.path.splitext(file.filename)[1].lower()
    if file_ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid file type. Allowed types: {', '.join(ALLOWED_EXTENSIONS)}"
        )


async def save_uploaded_file(file: UploadFile, subfolder: str = "") -> str:
    """
    Save uploaded file to disk

    Args:
        file: Uploaded file
        subfolder: Optional subfolder within uploads directory

    Returns:
        Relative path to saved file

    Raises:
        HTTPException: If save fails
    """
    try:
        # Validate file
        validate_image_file(file)

        # Create upload directory if not exists
        upload_dir = os.path.join(settings.UPLOAD_DIR, subfolder)
        os.makedirs(upload_dir, exist_ok=True)

        # Generate unique filename
        file_ext = os.path.splitext(file.filename)[1].lower()
        unique_filename = f"{uuid.uuid4()}{file_ext}"
        file_path = os.path.join(upload_dir, unique_filename)

        # Save file
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # Verify it's a valid image
        try:
            with Image.open(file_path) as img:
                img.verify()
        except Exception as e:
            # Delete invalid file
            os.remove(file_path)
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid or corrupted image file"
            )

        # Return relative path
        relative_path = os.path.join(subfolder, unique_filename)
        logger.info(f"File saved: {relative_path}")

        return relative_path

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"File save error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to save file"
        )


def delete_file(file_path: str) -> None:
    """
    Delete file from disk

    Args:
        file_path: Relative path to file
    """
    try:
        full_path = os.path.join(settings.UPLOAD_DIR, file_path)
        if os.path.exists(full_path):
            os.remove(full_path)
            logger.info(f"File deleted: {file_path}")
    except Exception as e:
        logger.warning(f"Failed to delete file {file_path}: {e}")


def get_full_path(relative_path: str) -> str:
    """
    Get full filesystem path from relative path

    Args:
        relative_path: Relative path within uploads directory

    Returns:
        Full filesystem path
    """
    return os.path.join(settings.UPLOAD_DIR, relative_path)
