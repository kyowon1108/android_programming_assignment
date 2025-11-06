"""
Pydantic Models for Request/Response Validation
API 요청/응답 데이터 검증 스키마
"""

from pydantic import BaseModel, EmailStr, Field
from typing import Optional, List
from datetime import date, datetime


# ==================== User Models ====================

class UserRegister(BaseModel):
    """User registration request"""
    email: EmailStr
    password: str = Field(..., min_length=6, max_length=100)
    nickname: str = Field(..., min_length=2, max_length=100)


class UserLogin(BaseModel):
    """User login request"""
    email: EmailStr
    password: str


class UserResponse(BaseModel):
    """User information response"""
    user_id: int
    email: str
    nickname: str
    profile_image_url: Optional[str] = None
    best_streak: int = 0
    current_streak: int = 0
    last_diary_date: Optional[date] = None
    created_at: datetime
    updated_at: datetime


class TokenResponse(BaseModel):
    """JWT token response"""
    access_token: str
    token_type: str = "bearer"
    user: UserResponse


# ==================== Diary Models ====================

class DiaryCreate(BaseModel):
    """Diary creation request"""
    date: date
    description: str = Field(..., min_length=1, max_length=5000)
    tag_ids: Optional[List[int]] = []


class DiaryUpdate(BaseModel):
    """Diary update request"""
    description: Optional[str] = Field(None, min_length=1, max_length=5000)
    tag_ids: Optional[List[int]] = None


class TagResponse(BaseModel):
    """Tag response"""
    tag_id: int
    tag_name: str
    tag_category: Optional[str] = None


class DiaryResponse(BaseModel):
    """Diary response with all information"""
    diary_id: int
    user_id: int
    date: date
    description: str
    photo_url: str
    vision_description: Optional[str] = None
    generated_story: Optional[str] = None
    expert_comment: Optional[str] = None
    emotion: Optional[str] = None
    year: int
    week_number: int
    tags: List[TagResponse] = []
    created_at: datetime
    updated_at: datetime


class DiaryListResponse(BaseModel):
    """List of diaries"""
    diaries: List[DiaryResponse]
    total: int
    page: int
    page_size: int


# ==================== Weekly Diary Models ====================

class WeeklyDiaryCreate(BaseModel):
    """Weekly diary creation request"""
    year: int = Field(..., ge=2000, le=2100)
    week_number: int = Field(..., ge=1, le=53)


class WeeklyDiaryResponse(BaseModel):
    """Weekly diary response"""
    week_id: int
    user_id: int
    year: int
    week_number: int
    start_date: date
    end_date: date
    weekly_summary_text: Optional[str] = None
    weekly_image_url: Optional[str] = None
    weekly_title: Optional[str] = None
    user_uploaded_image: bool = False
    created_at: datetime
    updated_at: datetime


class WeeklyDiaryFullResponse(WeeklyDiaryResponse):
    """Weekly diary with all daily diaries"""
    diaries: List[DiaryResponse] = []


# ==================== Generic Models ====================

class SuccessResponse(BaseModel):
    """Generic success response"""
    message: str
    data: Optional[dict] = None


class ErrorResponse(BaseModel):
    """Generic error response"""
    error: str
    detail: Optional[str] = None
