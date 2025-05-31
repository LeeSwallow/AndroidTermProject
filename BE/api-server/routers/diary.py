from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import extract
from typing import List
from datetime import date
from app.database import get_db
from app.models.diary import DbtDiary
from app.schemas.diary import (
    DbtDiaryCreate,
    DbtDiary as DbtDiarySchema,
    SyncRequest
)

router = APIRouter(
    prefix="/diaries",
    tags=["diaries"]
)


@router.post("/", response_model=DbtDiarySchema)
def create_diary(diary: DbtDiaryCreate, db: Session = Depends(get_db)):
    db_diary = DbtDiary(**diary.model_dump())
    db.add(db_diary)
    db.commit()
    db.refresh(db_diary)
    return db_diary


@router.get("/", response_model=List[DbtDiarySchema])
def get_diaries(
    skip: int = 0,
    limit: int = 100,
    start_date: date = None,
    end_date: date = None,
    db: Session = Depends(get_db)
):
    query = db.query(DbtDiary)
    
    if start_date:
        query = query.filter(DbtDiary.date >= start_date)
    if end_date:
        query = query.filter(DbtDiary.date <= end_date)
    
    return query.offset(skip).limit(limit).all()


@router.get("/{diary_id}", response_model=DbtDiarySchema)
def get_diary(diary_id: int, db: Session = Depends(get_db)):
    diary = db.query(DbtDiary).filter(DbtDiary.id == diary_id).first()
    if diary is None:
        raise HTTPException(status_code=404, detail="Diary not found")
    return diary


@router.get("/date/{date}", response_model=List[DbtDiarySchema])
def get_diaries_by_date(date: date, db: Session = Depends(get_db)):
    return db.query(DbtDiary).filter(DbtDiary.date == date).all()


@router.get("/month/{year}/{month}", response_model=List[DbtDiarySchema])
def get_diaries_by_month(
    year: int,
    month: int,
    db: Session = Depends(get_db)
):
    return db.query(DbtDiary).filter(
        extract('year', DbtDiary.date) == year,
        extract('month', DbtDiary.date) == month
    ).all()


@router.put("/{diary_id}", response_model=DbtDiarySchema)
def update_diary(
    diary_id: int,
    diary: DbtDiaryCreate,
    db: Session = Depends(get_db)
):
    db_diary = db.query(DbtDiary).filter(DbtDiary.id == diary_id).first()
    if db_diary is None:
        raise HTTPException(status_code=404, detail="Diary not found")
    
    for key, value in diary.model_dump().items():
        setattr(db_diary, key, value)
    
    db.commit()
    db.refresh(db_diary)
    return db_diary


@router.delete("/{diary_id}")
def delete_diary(diary_id: int, db: Session = Depends(get_db)):
    db_diary = db.query(DbtDiary).filter(DbtDiary.id == diary_id).first()
    if db_diary is None:
        raise HTTPException(status_code=404, detail="Diary not found")
    
    db.delete(db_diary)
    db.commit()
    return {"message": "Diary deleted successfully"}


@router.post("/sync", response_model=List[DbtDiarySchema])
def sync_diaries(sync_request: SyncRequest, db: Session = Depends(get_db)):
    # 마지막 동기화 이후 업데이트된 일기들을 가져옵니다
    updated_diaries = db.query(DbtDiary).filter(
        DbtDiary.updated_at > sync_request.last_sync_time
    ).order_by(DbtDiary.updated_at.desc()).all()
    
    # 동기화된 일기들의 synced 상태를 True로 업데이트합니다
    for diary in updated_diaries:
        diary.synced = True
    
    db.commit()
    return updated_diaries