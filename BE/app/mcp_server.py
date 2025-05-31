from fastmcp import FastMCP
from datetime import datetime
from app.dtos.diary import DiaryCreate, DiaryUpdate
from app.tools.diary_tools import (
    create_diary,
    get_diary,
    get_diaries_by_date,
    update_diary,
    delete_diary,
    get_monthly_diaries,
    get_emotion_stats
)

mcp = FastMCP(name="DBTDiaryServer")

@mcp.tool()
def create_diary_tool(diary: DiaryCreate):
    """새로운 dbt 감정 일기를 생성합니다."""
    return create_diary(diary)

@mcp.tool()
def get_diary_tool(diary_id: int):
    """특정 ID의 dbt 감정 일기를 조회합니다."""
    return get_diary(diary_id)

@mcp.tool()
def get_diaries_by_date_tool(target_date: str):
    """특정 날짜의 모든 dbt 감정 일기를 조회합니다."""
    date_obj = datetime.strptime(target_date, "%Y-%m-%d").date()
    return get_diaries_by_date(date_obj)

@mcp.tool()
def update_diary_tool(diary_id: int, updates: DiaryUpdate):
    """기존 dbt 감정 일기를 업데이트합니다."""
    return update_diary(diary_id, updates)

@mcp.tool()
def delete_diary_tool(diary_id: int):
    """dbt 감정 일기를 삭제합니다."""
    return delete_diary(diary_id)

@mcp.tool()
def get_monthly_diaries_tool(year: int, month: int):
    """특정 월의 모든 dbt 감정 일기를 조회합니다."""
    return get_monthly_diaries(year, month)

@mcp.tool()
def get_emotion_stats_tool(start_date: str, end_date: str):
    """특정 기간 동안의 dbt 감정 통계를 조회합니다."""
    start = datetime.strptime(start_date, "%Y-%m-%d").date()
    end = datetime.strptime(end_date, "%Y-%m-%d").date()
    return get_emotion_stats(start, end) 


mcp.run(host="0.0.0.0", port=8000)