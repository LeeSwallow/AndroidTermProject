from datetime import date, datetime
from typing import List, Optional, Dict, Any
import httpx


class DbtDiaryClient:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.client = httpx.AsyncClient(base_url=base_url)

    async def get_diary(self, diary_id: int) -> Dict[str, Any]:
        """특정 ID의 다이어리를 조회합니다."""
        response = await self.client.get(f"/api/diaries/{diary_id}")
        response.raise_for_status()
        return response.json()

    async def get_diaries(
        self,
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        sync_status: Optional[bool] = None
    ) -> List[Dict[str, Any]]:
        """다이어리 목록을 조회합니다."""
        params = {}
        if start_date:
            params["start_date"] = start_date.isoformat()
        if end_date:
            params["end_date"] = end_date.isoformat()
        if sync_status is not None:
            params["sync_status"] = str(sync_status).lower()

        response = await self.client.get("/api/diaries", params=params)
        response.raise_for_status()
        return response.json()

    async def create_diary(
        self,
        situation: str,
        emotion: str,
        intensity: int,
        thought: str,
        behavior: str,
        dbt_skill: str,
        sentiment: bool,
        diary_date: Optional[date] = None
    ) -> Dict[str, Any]:
        """새로운 다이어리를 생성합니다."""
        now = datetime.utcnow()
        data = {
            "date": (diary_date or date.today()).isoformat(),
            "situation": situation,
            "emotion": emotion,
            "intensity": intensity,
            "thought": thought,
            "behavior": behavior,
            "dbt_skill": dbt_skill,
            "sentiment": sentiment,
            "created_at": now.isoformat(),
            "updated_at": now.isoformat()
        }

        response = await self.client.post("/api/diaries", json=data)
        response.raise_for_status()
        return response.json()

    async def update_diary(
        self,
        diary_id: int,
        situation: Optional[str] = None,
        emotion: Optional[str] = None,
        intensity: Optional[int] = None,
        thought: Optional[str] = None,
        behavior: Optional[str] = None,
        dbt_skill: Optional[str] = None,
        sentiment: Optional[bool] = None,
        diary_date: Optional[date] = None
    ) -> Dict[str, Any]:
        """기존 다이어리를 수정합니다."""
        # 먼저 현재 데이터를 가져옵니다
        current_diary = await self.get_diary(diary_id)
        
        # 새로운 값으로 업데이트
        data = {
            "date": diary_date.isoformat() if diary_date else current_diary["date"],
            "situation": situation or current_diary["situation"],
            "emotion": emotion or current_diary["emotion"],
            "intensity": intensity if intensity is not None else current_diary["intensity"],
            "thought": thought or current_diary["thought"],
            "behavior": behavior or current_diary["behavior"],
            "dbt_skill": dbt_skill or current_diary["dbt_skill"],
            "sentiment": sentiment if sentiment is not None else current_diary["sentiment"],
            "created_at": current_diary["created_at"],
            "updated_at": datetime.utcnow().isoformat()
        }

        response = await self.client.put(f"/api/diaries/{diary_id}", json=data)
        response.raise_for_status()
        return response.json() 