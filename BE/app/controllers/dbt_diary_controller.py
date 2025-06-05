from typing import List
from datetime import date, datetime

from ..database.database import supabase
from ..models.dbt_diary import DbtDiary, DbtDiaryCreate


class DbtDiaryController:
    @staticmethod
    async def create_diary(diary: DbtDiaryCreate) -> DbtDiary:
        """새로운 다이어리 항목을 생성합니다."""
        data = diary.model_dump()
        data['created_at'] = data['created_at'].isoformat()
        data['updated_at'] = data['updated_at'].isoformat()
        data['date'] = data['date'].isoformat()
        
        result = supabase.table('dbt_diary').insert(data).execute()
        return DbtDiary(**result.data[0])

    @staticmethod
    async def get_diary(diary_id: int) -> DbtDiary:
        """특정 ID의 다이어리 항목을 조회합니다."""
        result = supabase.table('dbt_diary').select("*").eq('id', diary_id).execute()
        if not result.data:
            raise ValueError(f"Diary with id {diary_id} not found")
        return DbtDiary(**result.data[0])

    @staticmethod
    async def update_diary(diary_id: int, diary: DbtDiaryCreate) -> DbtDiary:
        """특정 ID의 다이어리 항목을 수정합니다."""
        data = diary.model_dump()
        data['created_at'] = data['created_at'].isoformat()
        data['updated_at'] = data['updated_at'].isoformat()
        data['date'] = data['date'].isoformat()
        
        result = supabase.table('dbt_diary').update(data).eq('id', diary_id).execute()
        if not result.data:
            raise ValueError(f"Diary with id {diary_id} not found")
        return DbtDiary(**result.data[0])

    @staticmethod
    async def get_diaries_by_date_range(
        start_date: date,
        end_date: date
    ) -> List[DbtDiary]:
        """특정 날짜 범위의 다이어리 항목들을 조회합니다."""
        result = supabase.table('dbt_diary')\
            .select("*")\
            .gte('date', start_date.isoformat())\
            .lte('date', end_date.isoformat())\
            .order('date', desc=True)\
            .execute()
        return [DbtDiary(**item) for item in result.data]

    @staticmethod
    async def get_unsynced_diaries() -> List[DbtDiary]:
        """동기화되지 않은 다이어리 항목들을 조회합니다."""
        result = supabase.table('dbt_diary')\
            .select("*")\
            .eq('is_synced', False)\
            .order('date', desc=True)\
            .execute()
        return [DbtDiary(**item) for item in result.data]

    @staticmethod
    async def sync_diary(diary: DbtDiaryCreate) -> DbtDiary:
        """단일 다이어리 항목을 Supabase에 동기화합니다."""
        data = diary.model_dump()
        data['created_at'] = data['created_at'].isoformat()
        data['updated_at'] = data['updated_at'].isoformat()
        data['date'] = data['date'].isoformat()
        data['is_synced'] = True
        
        result = supabase.table('dbt_diary').insert(data).execute()
        return DbtDiary(**result.data[0])

    @staticmethod
    async def sync_multiple_diaries(diaries: List[DbtDiaryCreate]) -> List[DbtDiary]:
        """여러 다이어리 항목을 Supabase에 동기화합니다."""
        data = []
        for diary in diaries:
            diary_dict = diary.model_dump()
            diary_dict['created_at'] = diary_dict['created_at'].isoformat()
            diary_dict['updated_at'] = diary_dict['updated_at'].isoformat()
            diary_dict['date'] = diary_dict['date'].isoformat()
            diary_dict['is_synced'] = True
            data.append(diary_dict)
        
        result = supabase.table('dbt_diary').insert(data).execute()
        return [DbtDiary(**item) for item in result.data]

    @staticmethod
    async def get_all_diaries() -> List[DbtDiary]:
        """모든 다이어리 항목을 조회합니다."""
        result = supabase.table('dbt_diary').select("*").execute()
        return [DbtDiary(**item) for item in result.data] 