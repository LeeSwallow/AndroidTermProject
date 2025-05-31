from typing import List, Dict, Any
from datetime import date
from sqlalchemy import extract
from app.database import SessionLocal
from app.models.diary import DbtDiary
from app.dtos.diary import DiaryCreate, DiaryUpdate, DiaryResponse


def create_diary(diary: DiaryCreate) -> Dict[str, Any]:
    """새로운 감정일기를 생성합니다."""
    db = SessionLocal()
    try:
        db_diary = DbtDiary(**diary.model_dump())
        db.add(db_diary)
        db.commit()
        db.refresh(db_diary)
        return {"message": "일기가 생성되었습니다.", "diary_id": db_diary.id}
    finally:
        db.close()


def get_diary(diary_id: int) -> Dict[str, Any]:
    """특정 ID의 일기를 조회합니다."""
    db = SessionLocal()
    try:
        diary = db.query(DbtDiary).filter(DbtDiary.id == diary_id).first()
        if not diary:
            return {"error": "일기를 찾을 수 없습니다."}
        return DiaryResponse.model_validate(diary).model_dump()
    finally:
        db.close()


def get_diaries_by_date(target_date: date) -> List[Dict[str, Any]]:
    """특정 날짜의 모든 일기를 조회합니다."""
    db = SessionLocal()
    try:
        diaries = db.query(DbtDiary).filter(DbtDiary.date == target_date).all()
        return [DiaryResponse.model_validate(diary).model_dump() for diary in diaries]
    finally:
        db.close()


def update_diary(diary_id: int, updates: DiaryUpdate) -> Dict[str, Any]:
    """기존 일기를 업데이트합니다."""
    db = SessionLocal()
    try:
        diary = db.query(DbtDiary).filter(DbtDiary.id == diary_id).first()
        if not diary:
            return {"error": "일기를 찾을 수 없습니다."}
        
        update_data = updates.model_dump(exclude_unset=True)
        for key, value in update_data.items():
            setattr(diary, key, value)
        
        db.commit()
        db.refresh(diary)
        return {"message": "일기가 업데이트되었습니다.", "diary": DiaryResponse.model_validate(diary).model_dump()}
    finally:
        db.close()


def delete_diary(diary_id: int) -> Dict[str, Any]:
    """일기를 삭제합니다."""
    db = SessionLocal()
    try:
        diary = db.query(DbtDiary).filter(DbtDiary.id == diary_id).first()
        if not diary:
            return {"error": "일기를 찾을 수 없습니다."}
        
        db.delete(diary)
        db.commit()
        return {"message": "일기가 삭제되었습니다."}
    finally:
        db.close()


def get_monthly_diaries(year: int, month: int) -> List[Dict[str, Any]]:
    """특정 월의 모든 일기를 조회합니다."""
    db = SessionLocal()
    try:
        diaries = db.query(DbtDiary).filter(
            extract('year', DbtDiary.date) == year,
            extract('month', DbtDiary.date) == month
        ).all()
        return [DiaryResponse.model_validate(diary).model_dump() for diary in diaries]
    finally:
        db.close()


def get_emotion_stats(start_date: date, end_date: date) -> Dict[str, Any]:
    """특정 기간 동안의 감정 통계를 조회합니다."""
    db = SessionLocal()
    try:
        diaries = db.query(DbtDiary).filter(
            DbtDiary.date >= start_date,
            DbtDiary.date <= end_date
        ).all()
        
        emotion_stats = {}
        for diary in diaries:
            if diary.emotion not in emotion_stats:
                emotion_stats[diary.emotion] = {
                    "count": 0,
                    "total_intensity": 0
                }
            emotion_stats[diary.emotion]["count"] += 1
            emotion_stats[diary.emotion]["total_intensity"] += diary.intensity
        
        # 평균 강도 계산
        for emotion in emotion_stats:
            emotion_stats[emotion]["average_intensity"] = (
                emotion_stats[emotion]["total_intensity"] / 
                emotion_stats[emotion]["count"]
            )
        
        return emotion_stats
    finally:
        db.close() 