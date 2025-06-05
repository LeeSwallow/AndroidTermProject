from functools import lru_cache
from typing import List
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # FastAPI 설정
    APP_HOST: str = "0.0.0.0"
    APP_PORT: int = 8080
    APP_DEBUG: bool = False
    APP_RELOAD: bool = False
    
    # 데이터베이스 설정
    DATABASE_URL: str
    POSTGRES_USER: str
    POSTGRES_PASSWORD: str
    POSTGRES_DB: str
    
    # Supabase 설정
    SUPABASE_URL: str
    SUPABASE_KEY: str
    
    # CORS 설정
    ALLOWED_ORIGINS: str
    
    # 보안 설정
    SECRET_KEY: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    
    @property
    def CORS_ORIGINS(self) -> List[str]:
        return self.ALLOWED_ORIGINS.split(",")
    
    class Config:
        env_file = ".env"
        case_sensitive = True


@lru_cache()
def get_settings() -> Settings:
    return Settings() 