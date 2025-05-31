from sqlalchemy import Column, Integer, String, Date, DateTime, Boolean
from sqlalchemy.sql import func
from app.database import Base


class DbtDiary(Base):
    __tablename__ = "dbt_diary"

    id = Column(Integer, primary_key=True, index=True)
    date = Column(Date, nullable=False)
    situation = Column(String, nullable=False)
    emotion = Column(String, nullable=False)
    intensity = Column(Integer, nullable=False)
    thought = Column(String, nullable=False)
    behavior = Column(String, nullable=False)
    dbt_skill = Column(String, nullable=False)
    synced = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
