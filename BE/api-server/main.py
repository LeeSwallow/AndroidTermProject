from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import diary
from app.database import engine
from app.models import diary as diary_models

# 데이터베이스 테이블 생성
diary_models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="DBT Diary API",
    description="DBT Diary Management API",
    version="1.0.0"
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 라우터 등록
app.include_router(diary.router)