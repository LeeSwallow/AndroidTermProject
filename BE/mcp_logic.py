import os
from dotenv import load_dotenv
from fastmcp import FastMCP
from supabase import create_client, Client
from pydantic import Field
from typing import Optional
from datetime import datetime

load_dotenv()
url: str = os.environ.get("SUPABASE_URL") or ""
key: str = os.environ.get("SUPABASE_KEY") or ""

if url == "" or key == "":
    raise ValueError("SUPABASE_URL or SUPABASE_KEY is not set")

supabase: Client = create_client(url, key)
table_name: str = "dbt_diary"

mcp = FastMCP(name="dbt-emotion-diary-mcp",
              instructions="""
                당신은 변증법적 행동치료(DBT) 전문 상담가의 역할을 수행하는 시스템입니다.  
                당신의 주요 임무는 다음과 같습니다:

                    - DBT 감정일기(DbtDiary) 데이터를 읽고, 내담자의 감정, 상황, 생각, 행동, DBT 스킬 사용 여부, 해결방안, 감정 강도, 감정의 긍정/부정 여부, 삭제 여부, 작성/수정 시각 등의 정보를 분석합니다.
                    - 내담자가 기록한 감정일기를 상담가의 시각에서 친절하고 공감적으로 해석하며, 내담자가 자신의 감정과 행동을 이해하고 변화할 수 있도록 돕는 피드백을 제공합니다.
                    - 감정일기 항목의 생성, 수정, 삭제 요청에 대해 내담자의 자기이해와 성장에 도움이 되는 방향으로 안내하고, 필요한 경우 DBT의 4대 모듈(마음챙김, 고통감내, 감정조절, 대인관계 효율성) 관련 조언을 제공합니다.
                    - 내담자가 작성한 일기의 내용을 바탕으로, 반복되는 패턴이나 감정, 행동의 변화를 함께 살펴보고, 긍정적인 변화와 자기돌봄을 격려합니다.
                    - 상담 과정에서 내담자의 자율성과 프라이버시를 존중하며, 항상 친절하고 비판단적인 태도로 소통합니다.
                    - 내담자가 일기 데이터를 수정하거나 삭제할 때, 그 이유와 감정적 변화에 대해 함께 탐색하며, 자기결정권을 존중하는 피드백을 제공합니다.
                
                이 시스템은 DBT 감정일기(DbtDiary) 엔티티의 모든 필드를 활용하여 내담자의 자기이해와 변화를 촉진하는 상담가 역할을 수행합니다.
              """)

@mcp.tool(
    name="get_today_date",
    description="오늘 날짜를 조회합니다."
)
def get_today_date() -> str:
    """
    오늘 날짜를 조회합니다.
    """
    return datetime.now().strftime("%Y-%m-%d")


@mcp.tool(
    name="get_dbt_diaries",
    description="특정 기간(from_date~to_date) 동안의 DBT 감정일기 목록을 조회합니다."
)
async def get_dbt_diaries(
    from_date: str = Field(..., description="조회 시작 날짜(YYYY-MM-DD)"),
    to_date: str = Field(..., description="조회 종료 날짜(YYYY-MM-DD)")
) -> list:
    """
    주어진 기간(from_date~to_date) 동안의 DBT 감정일기(DbtDiary) 목록을 조회합니다.
    """
    response = supabase.table("DbtDiary").select("*").gte("created_at", from_date).lte("created_at", to_date).execute()
    return response.data

@mcp.tool(
    name="get_dbt_diary_by_date",
    description="특정 날짜의 DBT 감정일기 항목을 조회합니다."
)
async def get_dbt_diary_by_date(
    date: str = Field(..., description="조회할 날짜(YYYY-MM-DD)")
) -> list:
    """
    주어진 날짜(date)에 해당하는 DBT 감정일기(DbtDiary) 항목을 조회합니다.
    """
    response = supabase.table(table_name).select("*").eq("date", date).execute()
    return response.data


@mcp.tool(
    name="create_dbt_diary",
    description="DBT 감정일기 항목을 새로 생성합니다."
)
async def create_dbt_diary(
    date: str = Field(..., description="일기 날짜(YYYY-MM-DD)"),
    situation: str = Field(..., description="상황 설명"),
    emotion: str = Field(..., description="느낀 감정"),
    intensity: int = Field(..., description="감정 강도(1~10)"),
    dbt_skill: str = Field(..., description="사용한 DBT 스킬"),
    sentiment: bool = Field(..., description="감정의 긍정/부정 여부(True=긍정, False=부정)"),
    thought: Optional[str] = Field("", description="생각"),
    behavior: Optional[str] = Field("", description="행동"),
    solution: Optional[str] = Field("", description="해결방안")
) -> list:
    """
    입력받은 값으로 새로운 DBT 감정일기(DbtDiary) 항목을 생성합니다.
    """
    
    if get_dbt_diary_by_date(date):
        return "이미 해당 날짜의 일기가 존재합니다."

    data = {
        "date": date,
        "situation": situation,
        "emotion": emotion,
        "intensity": intensity,
        "thought": thought,
        "behavior": behavior,
        "dbt_skill": dbt_skill,
        "solution": solution,
        "sentiment": sentiment
    }
    response = supabase.table(table_name).insert(data).execute()
    return response.data

@mcp.tool(
    name="update_dbt_diary",
    description="DBT 감정일기 항목을 수정합니다."
)
async def update_dbt_diary(
    date: str = Field(..., description="수정할 DBT 감정일기 날짜(YYYY-MM-DD)"),
    situation: Optional[str] = Field(None, description="상황 설명"),
    emotion: Optional[str] = Field(None, description="느낀 감정"),
    intensity: Optional[int] = Field(None, description="감정 강도(1~10)"),
    thought: Optional[str] = Field(None, description="생각"),
    behavior: Optional[str] = Field(None, description="행동"),
    dbt_skill: Optional[str] = Field(None, description="사용한 DBT 스킬"),
    solution: Optional[str] = Field(None, description="해결방안"),
    sentiment: Optional[bool] = Field(None, description="감정의 긍정/부정 여부(True=긍정, False=부정)")
) -> list:
    """
    날짜로 지정한 DBT 감정일기 항목의 값을 입력받은 필드만 수정합니다.
    """
    update_fields = {}
    if situation is not None:
        update_fields["situation"] = situation
    if emotion is not None:
        update_fields["emotion"] = emotion
    if intensity is not None:
        update_fields["intensity"] = intensity
    if thought is not None:
        update_fields["thought"] = thought
    if behavior is not None:
        update_fields["behavior"] = behavior
    if dbt_skill is not None:
        update_fields["dbt_skill"] = dbt_skill
    if solution is not None:
        update_fields["solution"] = solution
    if sentiment is not None:
        update_fields["sentiment"] = sentiment
    update_fields["updated_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    if not update_fields:
        return []  # 아무것도 수정하지 않음
    response = supabase.table(table_name).update(update_fields).eq("date", date).execute()
    return response.data

@mcp.tool(
    name="delete_dbt_diary",
    description="DBT 감정일기 항목을 삭제(soft delete)합니다. 실제로 삭제하지 않고 deleted=true로 변경합니다."
)
async def delete_dbt_diary(
    date: str = Field(..., description="삭제할 DBT 감정일기 날짜(YYYY-MM-DD)")
) -> list:
    """
    날짜로 지정한 DBT 감정일기 항목을 실제로 삭제하지 않고 deleted=true로 변경(soft delete)합니다.
    """
    response = supabase.table(table_name).update({"deleted": True}).eq("date", date).execute()
    return response.data

@mcp.prompt
def dbt_counseler_role():
    """
    dbt 상담가로서의 역할에 대한 설명을 제공합니다.
    """
    return """
    당신은 변증법적 행동치료(DBT) 전문 상담가의 역할을 수행하는 시스템입니다.  
    상담 대상자의 감정일기를 분석하고, 내담자의 자기이해와 성장을 돕는 피드백을 제공합니다.
    내담자의 감정일기를 분석할 때, 내담자의 감정, 상황, 생각, 행동, DBT 스킬 사용 여부, 해결방안, 감정 강도, 감정의 긍정/부정 여부, 삭제 여부, 작성/수정 시각 등의 정보를 분석합니다.
    내담자가 기록한 감정일기를 상담가의 시각에서 친절하고 공감적으로 해석하며, 내담자가 자신의 감정과 행동을 이해하고 변화할 수 있도록 돕는 피드백을 제공합니다.
    감정일기 항목의 생성, 수정, 삭제 요청에 대해 내담자의 자기이해와 성장에 도움이 되는 방향으로 안내하고, 필요한 경우 DBT의 4대 모듈(마음챙김, 고통감내, 감정조절, 대인관계 효율성) 관련 조언을 제공합니다.
    내담자가 작성한 일기의 내용을 바탕으로, 반복되는 패턴이나 감정, 행동의 변화를 함께 살펴보고, 긍정적인 변화와 자기돌봄을 격려합니다.
    상담 과정에서 내담자의 자율성과 프라이버시를 존중하며, 항상 친절하고 비판단적인 태도로 소통합니다.
    """