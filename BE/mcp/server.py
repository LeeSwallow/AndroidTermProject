import os
from fastmcp import FastMCP
from typing import Optional, Annotated
from datetime import date
from pydantic import Field
from .client import DbtDiaryClient


# 기본 환경변수 설정
default_env = {
    "APPLICATION_URL": "http://localhost:8080",
    "CURSOR_ENABLED": "true",
    "CURSOR_URL": "http://localhost:8000",
    "HOST": "0.0.0.0",
    "PORT": "8000"
}

# 환경변수 설정
if os.path.exists(".env"):
    from dotenv import load_dotenv
    load_dotenv()
else:
    os.environ.update(default_env)

# 전역 변수 초기화
APPLICATION_URL = os.getenv("APPLICATION_URL", "http://localhost:8080")
client = DbtDiaryClient(APPLICATION_URL)


mcp = FastMCP(
    name="DbtDiaryServer",
    instructions="""
    당신은 DBT(Dialectical Behavior Therapy) 다이어리 애플리케이션의 커뮤니케이션을 도와주는 챗봇입니다.
    사용자의 요청에 따라 감정 일기를 조회하고, 작성하고, 수정하는 것을 도와줍니다.
    DBT 기술을 활용한 감정 조절과 마음 챙김을 돕는 것이 주요 목적입니다.

    다음과 같은 기능들을 제공합니다:
    1. 다이어리 조회 (단일/목록)
    2. 다이어리 작성
    3. 다이어리 수정
    4. 날짜 범위로 다이어리 검색
    5. 동기화 상태로 다이어리 필터링

    각 다이어리는 다음 정보를 포함합니다:
    - 날짜
    - 상황
    - 감정과 강도(1-10)
    - 생각
    - 행동
    - 사용한 DBT 기술
    - 감정 상태(긍정/부정)
    """
)


@mcp.tool(
    name="get_diary",
    description="특정 ID의 DBT 다이어리를 조회하는 도구입니다."
)
async def get_diary(
    diary_id: Annotated[int, Field(description="조회할 다이어리 ID", ge=1)]
) -> str:
    """특정 다이어리를 조회합니다."""
    try:
        diary = await client.get_diary(diary_id)
        return (
            f"다이어리 조회 결과:\n"
            f"날짜: {diary['date']}\n"
            f"상황: {diary['situation']}\n"
            f"감정: {diary['emotion']} (강도: {diary['intensity']})\n"
            f"생각: {diary['thought']}\n"
            f"행동: {diary['behavior']}\n"
            f"DBT 기술: {diary['dbt_skill']}\n"
            f"감정 상태: {'긍정적' if diary['sentiment'] else '부정적'}"
        )
    except Exception as e:
        return f"조회 실패: {str(e)}"


@mcp.tool(
    name="get_diaries",
    description="""
    DBT 다이어리 목록을 조회하는 도구입니다.
    날짜 범위로 필터링할 수 있으며, 동기화 상태로도 필터링 가능합니다.
    """
)
async def get_diaries(
    start_date: Annotated[Optional[str], Field(
        description="시작 날짜 (YYYY-MM-DD)",
        pattern=r"^\d{4}-\d{2}-\d{2}$"
    )] = None,
    end_date: Annotated[Optional[str], Field(
        description="종료 날짜 (YYYY-MM-DD)",
        pattern=r"^\d{4}-\d{2}-\d{2}$"
    )] = None,
    sync_status: Annotated[Optional[bool], Field(
        description="동기화 상태 (true/false)"
    )] = None
) -> str:
    """다이어리 목록을 조회합니다."""
    try:
        # 날짜 변환
        start = date.fromisoformat(start_date) if start_date else None
        end = date.fromisoformat(end_date) if end_date else None
        
        diaries = await client.get_diaries(
            start_date=start,
            end_date=end,
            sync_status=sync_status
        )
        
        if not diaries:
            return "조회된 다이어리가 없습니다."
            
        result = "다이어리 목록:\n"
        for diary in diaries:
            result += (
                f"- [{diary['date']}] "
                f"{diary['emotion']}({diary['intensity']}): "
                f"{diary['situation']}\n"
            )
            
        return result
        
    except Exception as e:
        return f"조회 실패: {str(e)}"


@mcp.tool(
    name="create_diary",
    description="새로운 DBT 다이어리를 작성하는 도구입니다."
)
async def create_diary(
    situation: Annotated[str, Field(
        description="상황 설명",
        min_length=1,
        max_length=500
    )],
    emotion: Annotated[str, Field(
        description="감정",
        min_length=1,
        max_length=100
    )],
    intensity: Annotated[int, Field(
        description="감정 강도 (1-10)",
        ge=1,
        le=10
    )],
    thought: Annotated[str, Field(
        description="생각",
        min_length=1,
        max_length=500
    )],
    behavior: Annotated[str, Field(
        description="행동",
        min_length=1,
        max_length=500
    )],
    dbt_skill: Annotated[str, Field(
        description="사용한 DBT 기술",
        min_length=1,
        max_length=100
    )],
    sentiment: Annotated[bool, Field(
        description="감정 상태 (true: 긍정적, false: 부정적)"
    )],
    diary_date: Annotated[Optional[str], Field(
        description="다이어리 날짜 (YYYY-MM-DD), 비워두면 오늘 날짜",
        pattern=r"^\d{4}-\d{2}-\d{2}$"
    )] = None
) -> str:
    """새로운 다이어리를 작성합니다."""
    try:
        # 날짜 변환
        diary_date_obj = (
            date.fromisoformat(diary_date) if diary_date else None
        )
        
        diary = await client.create_diary(
            situation=situation,
            emotion=emotion,
            intensity=intensity,
            thought=thought,
            behavior=behavior,
            dbt_skill=dbt_skill,
            sentiment=sentiment,
            diary_date=diary_date_obj
        )
        
        return (
            f"다이어리가 작성되었습니다!\n"
            f"날짜: {diary['date']}\n"
            f"감정: {diary['emotion']} (강도: {diary['intensity']})\n"
            f"상황: {diary['situation']}\n"
            f"DBT 기술: {diary['dbt_skill']}"
        )
        
    except Exception as e:
        return f"작성 실패: {str(e)}"


@mcp.tool(
    name="update_diary",
    description="""
    기존 DBT 다이어리를 수정하는 도구입니다.
    수정하지 않을 필드는 비워두면 됩니다.
    """
)
async def update_diary(
    diary_id: Annotated[int, Field(
        description="수정할 다이어리 ID",
        ge=1
    )],
    situation: Annotated[Optional[str], Field(
        description="새로운 상황 설명",
        min_length=1,
        max_length=500
    )] = None,
    emotion: Annotated[Optional[str], Field(
        description="새로운 감정",
        min_length=1,
        max_length=100
    )] = None,
    intensity: Annotated[Optional[int], Field(
        description="새로운 감정 강도 (1-10)",
        ge=1,
        le=10
    )] = None,
    thought: Annotated[Optional[str], Field(
        description="새로운 생각",
        min_length=1,
        max_length=500
    )] = None,
    behavior: Annotated[Optional[str], Field(
        description="새로운 행동",
        min_length=1,
        max_length=500
    )] = None,
    dbt_skill: Annotated[Optional[str], Field(
        description="새로운 DBT 기술",
        min_length=1,
        max_length=100
    )] = None,
    sentiment: Annotated[Optional[bool], Field(
        description="새로운 감정 상태 (true: 긍정적, false: 부정적)"
    )] = None,
    diary_date: Annotated[Optional[str], Field(
        description="새로운 다이어리 날짜 (YYYY-MM-DD)",
        pattern=r"^\d{4}-\d{2}-\d{2}$"
    )] = None
) -> str:
    """기존 다이어리를 수정합니다."""
    try:
        # 날짜 변환
        diary_date_obj = (
            date.fromisoformat(diary_date) if diary_date else None
        )
        
        diary = await client.update_diary(
            diary_id=diary_id,
            situation=situation,
            emotion=emotion,
            intensity=intensity,
            thought=thought,
            behavior=behavior,
            dbt_skill=dbt_skill,
            sentiment=sentiment,
            diary_date=diary_date_obj
        )
        
        return (
            f"다이어리가 수정되었습니다!\n"
            f"날짜: {diary['date']}\n"
            f"감정: {diary['emotion']} (강도: {diary['intensity']})\n"
            f"상황: {diary['situation']}\n"
            f"DBT 기술: {diary['dbt_skill']}"
        )
        
    except Exception as e:
        return f"수정 실패: {str(e)}"


if __name__ == "__main__":
    import platform
    
    # Windows에서 실행 시 특별한 설정 추가
    if platform.system() == "Windows":
        import asyncio
        asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
    
    # STDIO 트랜스포트로 서버 실행
    mcp.run(transport="streamable-http", host="127.0.0.1", port=8000)
