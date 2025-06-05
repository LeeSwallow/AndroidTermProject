from functools import lru_cache
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # MCP 서버 설정
    MCP_HOST: str = "0.0.0.0"
    MCP_PORT: int = 8000
    APPLICATION_URL: str = "http://localhost:8080"
    
    class Config:
        env_file = ".env"
        case_sensitive = True


@lru_cache()
def get_settings() -> Settings:
    return Settings() 