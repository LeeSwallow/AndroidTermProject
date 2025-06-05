from datetime import date, datetime
from pydantic import BaseModel

class DbtDiaryBase(BaseModel):
    date: date
    situation: str
    emotion: str
    intensity: int
    thought: str
    behavior: str
    dbt_skill: str
    sentiment: bool
    is_synced: bool = False
    created_at: datetime
    updated_at: datetime


class DbtDiaryCreate(DbtDiaryBase):
    pass


class DbtDiary(DbtDiaryBase):
    id: int

    class Config:
        from_attributes = True