from pydantic import BaseModel, Field
from typing import Optional
from datetime import date


class DiaryCreate(BaseModel):
    date: date
    situation: str
    emotion: str
    intensity: int = Field(ge=1, le=10)
    thought: str
    behavior: str
    dbt_skill: str


class DiaryUpdate(BaseModel):
    situation: Optional[str] = None
    emotion: Optional[str] = None
    intensity: Optional[int] = Field(None, ge=1, le=10)
    thought: Optional[str] = None
    behavior: Optional[str] = None
    dbt_skill: Optional[str] = None


class DiaryResponse(BaseModel):
    id: int
    date: date
    situation: str
    emotion: str
    intensity: int
    thought: str
    behavior: str
    dbt_skill: str
    synced: bool
    created_at: date
    updated_at: Optional[date] = None 