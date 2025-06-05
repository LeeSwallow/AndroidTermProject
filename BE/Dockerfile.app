FROM python:3.13.4-slim-bookworm

WORKDIR /app

# 시스템 의존성 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Python 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY app/ app/
COPY alembic.ini .
COPY migrations/ migrations/

# 환경 변수 설정
ENV HOST=0.0.0.0
ENV PORT=8080

# 포트 노출
EXPOSE 8080

# 실행
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"] 