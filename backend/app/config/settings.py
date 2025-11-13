"""
Application Settings Configuration
환경 변수 및 애플리케이션 설정 관리
"""

from pydantic_settings import BaseSettings
from typing import List


class Settings(BaseSettings):
    """Application settings loaded from environment variables"""

    # Server Configuration
    ENVIRONMENT: str = "development"
    HOST: str = "0.0.0.0"
    PORT: int = 8000

    # Database Configuration
    DB_HOST: str = "localhost"
    DB_PORT: int = 5432
    DB_NAME: str = "baby_diary"
    DB_USER: str = "postgres"
    DB_PASSWORD: str = ""

    # JWT Configuration
    JWT_SECRET: str = "your_super_secret_jwt_key_change_this_in_production"
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRATION_HOURS: int = 720  # 30 days

    # Google API Configuration
    GOOGLE_VISION_API_KEY: str = ""
    GOOGLE_GEMINI_API_KEY: str = ""

    # File Upload Configuration
    UPLOAD_DIR: str = "./uploads"
    MAX_FILE_SIZE: int = 10485760  # 10MB

    # CORS Configuration
    CORS_ORIGINS: str = "http://localhost:8080,http://127.0.0.1:8080"

    # Logging Configuration
    LOG_LEVEL: str = "INFO"

    # Mock AI Mode (for testing without API keys)
    USE_MOCK_AI: bool = False  # Set to True to use mock data instead of real APIs

    @property
    def database_url(self) -> str:
        """Generate PostgreSQL connection string"""
        return f"postgresql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"

    @property
    def cors_origins_list(self) -> List[str]:
        """Convert CORS_ORIGINS string to list"""
        return [origin.strip() for origin in self.CORS_ORIGINS.split(",")]

    class Config:
        env_file = ".env"
        case_sensitive = True


# Global settings instance
settings = Settings()
