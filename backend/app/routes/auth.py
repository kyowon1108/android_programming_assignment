"""
Authentication Routes
사용자 인증 관련 API 엔드포인트
"""

from fastapi import APIRouter, HTTPException, status, Depends, Header
from typing import Optional
import logging

from app.models.schemas import UserRegister, UserLogin, TokenResponse, UserResponse
from app.config.database import get_db
from app.utils.auth import (
    hash_password,
    verify_password,
    create_access_token,
    get_current_user_id,
    refresh_token_if_needed
)

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])


@router.post("/register", response_model=TokenResponse, status_code=status.HTTP_201_CREATED)
async def register(user_data: UserRegister, db=Depends(get_db)):
    """
    Register new user

    - **email**: Valid email address (unique)
    - **password**: Password (min 6 characters)
    - **nickname**: Display name (2-100 characters)
    """
    try:
        with db.get_cursor() as cursor:
            # Check if email already exists
            cursor.execute("SELECT user_id FROM users WHERE email = %s", (user_data.email,))
            if cursor.fetchone():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Email already registered"
                )

            # Hash password and create user
            hashed_pw = hash_password(user_data.password)

            cursor.execute(
                """
                INSERT INTO users (email, password_hash, nickname)
                VALUES (%s, %s, %s)
                RETURNING user_id, email, nickname, profile_image_url,
                          best_streak, current_streak, last_diary_date,
                          created_at, updated_at
                """,
                (user_data.email, hashed_pw, user_data.nickname)
            )

            user_row = cursor.fetchone()

            # Create access token
            access_token = create_access_token(
                data={"user_id": user_row["user_id"], "email": user_row["email"]}
            )

            user_response = UserResponse(**user_row)

            return TokenResponse(access_token=access_token, user=user_response)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Registration error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Registration failed"
        )


@router.post("/login", response_model=TokenResponse)
async def login(credentials: UserLogin, db=Depends(get_db)):
    """
    User login

    - **email**: Registered email address
    - **password**: User password

    Returns JWT access token and user information
    """
    try:
        with db.get_cursor() as cursor:
            # Get user by email
            cursor.execute(
                """
                SELECT user_id, email, password_hash, nickname, profile_image_url,
                       best_streak, current_streak, last_diary_date,
                       created_at, updated_at
                FROM users
                WHERE email = %s
                """,
                (credentials.email,)
            )

            user_row = cursor.fetchone()

            if not user_row:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid email or password"
                )

            # Verify password
            if not verify_password(credentials.password, user_row["password_hash"]):
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid email or password"
                )

            # Create access token
            access_token = create_access_token(
                data={"user_id": user_row["user_id"], "email": user_row["email"]}
            )

            # Remove password_hash from response
            user_dict = dict(user_row)
            del user_dict["password_hash"]
            user_response = UserResponse(**user_dict)

            return TokenResponse(access_token=access_token, user=user_response)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Login error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Login failed"
        )


@router.get("/me", response_model=UserResponse)
async def get_current_user(
    user_id: int = Depends(get_current_user_id),
    db=Depends(get_db)
):
    """
    Get current user information

    Requires: Bearer token in Authorization header
    """
    try:
        with db.get_cursor() as cursor:
            cursor.execute(
                """
                SELECT user_id, email, nickname, profile_image_url,
                       best_streak, current_streak, last_diary_date,
                       created_at, updated_at
                FROM users
                WHERE user_id = %s
                """,
                (user_id,)
            )

            user_row = cursor.fetchone()

            if not user_row:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="User not found"
                )

            return UserResponse(**user_row)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Get user error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve user information"
        )


@router.post("/refresh")
async def refresh_token(authorization: Optional[str] = Header(None)):
    """
    Refresh JWT token if close to expiration

    Returns new token if refresh is needed, or message if not needed
    """
    new_token = refresh_token_if_needed(authorization)

    if new_token:
        return {"access_token": new_token, "token_type": "bearer", "refreshed": True}
    else:
        return {"message": "Token is still valid", "refreshed": False}
