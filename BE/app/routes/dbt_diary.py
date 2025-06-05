from fastapi import APIRouter, HTTPException
from typing import List
from datetime import date

from ..controllers.dbt_diary_controller import DbtDiaryController
from ..models.dbt_diary import DbtDiary, DbtDiaryCreate

router = APIRouter(
    prefix="/api/diaries",
    tags=["diaries"]
)


@router.post("/", response_model=DbtDiary)
async def create_diary(diary: DbtDiaryCreate):
    """새로운 다이어리 항목을 생성합니다."""
    try:
        return await DbtDiaryController.create_diary(diary)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{diary_id}", response_model=DbtDiary)
async def get_diary(diary_id: int):
    """특정 ID의 다이어리 항목을 조회합니다."""
    try:
        return await DbtDiaryController.get_diary(diary_id)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{diary_id}", response_model=DbtDiary)
async def update_diary(diary_id: int, diary: DbtDiaryCreate):
    """특정 ID의 다이어리 항목을 수정합니다."""
    try:
        return await DbtDiaryController.update_diary(diary_id, diary)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/", response_model=List[DbtDiary])
async def get_diaries(
    start_date: date | None = None,
    end_date: date | None = None,
    sync_status: bool | None = None
):
    """다이어리 항목들을 조회합니다.
    - start_date와 end_date가 모두 제공되면 해당 기간의 항목들을 조회
    - sync_status가 제공되면 해당 동기화 상태의 항목들을 조회
    - 파라미터가 없으면 전체 항목 조회
    """
    try:
        if start_date and end_date:
            return await DbtDiaryController.get_diaries_by_date_range(
                start_date, end_date
            )
        elif sync_status is not None:
            if not sync_status:  # False인 경우만 미동기화 항목 조회
                return await DbtDiaryController.get_unsynced_diaries()
            # True인 경우는 추후 필요시 구현
            raise HTTPException(
                status_code=400,
                detail="Synced diaries query not implemented"
            )
        else:
            return await DbtDiaryController.get_all_diaries()
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/{diary_id}/sync", response_model=DbtDiary)
async def sync_diary(diary_id: int, diary: DbtDiaryCreate):
    """특정 다이어리 항목을 동기화합니다."""
    try:
        return await DbtDiaryController.sync_diary(diary)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/sync-batch", response_model=List[DbtDiary])
async def sync_multiple_diaries(diaries: List[DbtDiaryCreate]):
    """여러 다이어리 항목을 일괄 동기화합니다."""
    try:
        return await DbtDiaryController.sync_multiple_diaries(diaries)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))