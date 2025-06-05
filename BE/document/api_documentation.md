# DBT Diary Sync API 문서

## 개요
이 API는 DBT(Dialectical Behavior Therapy) 다이어리 앱의 데이터를 Supabase 서버와 동기화하기 위한 엔드포인트를 제공합니다.

## 기본 정보
- 기본 URL: `http://localhost:8080`
- Content-Type: `application/json`

## 엔드포인트

### 1. 다이어리 생성
새로운 다이어리 항목을 생성합니다.

- **URL**: `/api/diaries`
- **Method**: `POST`
- **Request Body**:
```json
{
    "date": "2024-01-01",
    "situation": "시험 전날",
    "emotion": "불안",
    "intensity": 7,
    "thought": "시험을 잘 볼 수 있을까?",
    "behavior": "심호흡 하기",
    "dbt_skill": "마음챙김",
    "sentiment": true,
    "created_at": "2024-01-01T12:00:00Z",
    "updated_at": "2024-01-01T12:00:00Z"
}
```
- **Response**: `201 Created`
```json
{
    "id": 1,
    "date": "2024-01-01",
    "situation": "시험 전날",
    "emotion": "불안",
    "intensity": 7,
    "thought": "시험을 잘 볼 수 있을까?",
    "behavior": "심호흡 하기",
    "dbt_skill": "마음챙김",
    "sentiment": true,
    "is_synced": false,
    "created_at": "2024-01-01T12:00:00Z",
    "updated_at": "2024-01-01T12:00:00Z"
}
```

### 2. 단일 다이어리 조회
특정 ID의 다이어리 항목을 조회합니다.

- **URL**: `/api/diaries/{diary_id}`
- **Method**: `GET`
- **Response**: `200 OK`
```json
{
    "id": 1,
    "date": "2024-01-01",
    "situation": "시험 전날",
    "emotion": "불안",
    "intensity": 7,
    "thought": "시험을 잘 볼 수 있을까?",
    "behavior": "심호흡 하기",
    "dbt_skill": "마음챙김",
    "sentiment": true,
    "is_synced": false,
    "created_at": "2024-01-01T12:00:00Z",
    "updated_at": "2024-01-01T12:00:00Z"
}
```

### 3. 다이어리 수정
특정 ID의 다이어리 항목을 수정합니다.

- **URL**: `/api/diaries/{diary_id}`
- **Method**: `PUT`
- **Request Body**: 생성 요청과 동일
- **Response**: `200 OK` (응답 형식은 조회와 동일)

### 4. 다이어리 목록 조회
다이어리 항목들을 조회합니다. 쿼리 파라미터를 통해 필터링이 가능합니다.

- **URL**: `/api/diaries`
- **Method**: `GET`
- **Query Parameters**:
  - `start_date`: 시작 날짜 (YYYY-MM-DD)
  - `end_date`: 종료 날짜 (YYYY-MM-DD)
  - `sync_status`: 동기화 상태 (true/false)
- **Response**: `200 OK`
```json
[
    {
        "id": 1,
        "date": "2024-01-01",
        "situation": "시험 전날",
        "emotion": "불안",
        "intensity": 7,
        "thought": "시험을 잘 볼 수 있을까?",
        "behavior": "심호흡 하기",
        "dbt_skill": "마음챙김",
        "sentiment": true,
        "is_synced": false,
        "created_at": "2024-01-01T12:00:00Z",
        "updated_at": "2024-01-01T12:00:00Z"
    }
]
```

### 5. 다이어리 동기화
특정 다이어리 항목을 동기화합니다.

- **URL**: `/api/diaries/{diary_id}/sync`
- **Method**: `POST`
- **Request Body**: 생성 요청과 동일
- **Response**: `200 OK` (응답 형식은 조회와 동일)

### 6. 다이어리 일괄 동기화
여러 다이어리 항목을 한 번에 동기화합니다.

- **URL**: `/api/diaries/sync-batch`
- **Method**: `POST`
- **Request Body**: 다이어리 항목 배열
- **Response**: `200 OK` (응답 형식은 목록 조회와 동일)

## 에러 응답
API는 다음과 같은 형식으로 에러를 반환합니다:

```json
{
    "detail": "에러 메시지"
}
```

### 일반적인 에러 코드
- `400 Bad Request`: 잘못된 요청 형식
- `404 Not Found`: 요청한 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

## 데이터 모델

### DbtDiary
| 필드 | 타입 | 설명 |
|------|------|------|
| id | integer | 고유 식별자 (자동 생성) |
| date | date | 다이어리 작성 날짜 |
| situation | string | 상황 설명 |
| emotion | string | 감정 |
| intensity | integer | 감정 강도 (1-10) |
| thought | string | 생각 |
| behavior | string | 행동 |
| dbt_skill | string | 사용한 DBT 기술 |
| sentiment | boolean | 긍정/부정 감정 |
| is_synced | boolean | 동기화 여부 |
| created_at | datetime | 생성 시간 |
| updated_at | datetime | 수정 시간 | 