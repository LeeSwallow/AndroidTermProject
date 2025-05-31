from pydantic import BaseModel
from datetime import date, datetime
from typing import Optional


class DbtDiaryBase(BaseModel):
    date: date
    situation: str
    emotion: str
    intensity: int
    thought: str
    behavior: str
    dbt_skill: str
    synced: bool = False


class DbtDiaryCreate(DbtDiaryBase):
    pass


class DbtDiary(DbtDiaryBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class SyncRequest(BaseModel):
    last_sync_time: datetime 