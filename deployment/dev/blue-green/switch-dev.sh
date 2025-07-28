#!/bin/bash

set -e

echo "========================================"
echo "[DEV DEPLOY] Blue/Green 무중단 배포 시작"
echo "========================================"

# 상태 파일 경로
DEPLOY_STATE_DIR="/home/ubuntu/color-config"
CURRENT_COLOR_FILE="$DEPLOY_STATE_DIR/current_color_dev"

mkdir -p "$DEPLOY_STATE_DIR"
if [ ! -f "$CURRENT_COLOR_FILE" ]; then
  echo "blue" > "$CURRENT_COLOR_FILE"
fi

CURRENT=$(cat "$CURRENT_COLOR_FILE")
if [ "$CURRENT" == "blue" ]; then
  NEXT="green"
else
  NEXT="blue"
fi

NEXT_COMPOSE="../docker/docker-compose-${NEXT}-dev.yml"
BACKEND_NAME="backend-${NEXT}-dev"

echo "[INFO] 현재 배포 중인 컨테이너: $CURRENT"
echo "[INFO] 전환할 컨테이너: $NEXT"

# 새 컨테이너 실행
docker compose -f "$NEXT_COMPOSE" up -d --build

# Health Check
echo "[INFO] Health Check 진행 중: $BACKEND_NAME ..."
for i in {1..20}; do
  STATUS=$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND_NAME" 2>/dev/null || echo "null")
  if [ "$STATUS" == "\"healthy\"" ]; then
    echo "[SUCCESS] $BACKEND_NAME 컨테이너가 정상입니다."
    break
  else
    echo "  [$i/20] 아직 준비되지 않음... (상태: $STATUS)"
    sleep 5
  fi
done

# 실패 시 롤백
if [ "$STATUS" != "\"healthy\"" ]; then
  echo "[ERROR] $BACKEND_NAME 실행 실패 → 롤백"
  docker rm -f "$BACKEND_NAME" || true
  exit 1
fi

# NGINX upstream 파일 업데이트
NGINX_CONF_PATH="../nginx/upstream-blue-green-dev.conf"
echo "[INFO] Nginx upstream 설정 갱신 → $BACKEND_NAME"

cat > "$NGINX_CONF_PATH" <<EOF
upstream backend-dev {
  server $BACKEND_NAME:8080;
}

server {
  listen 80;
  server_name dataracy.store;

  location / {
    proxy_pass http://backend-dev;
    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
  }

  location /actuator/health {
    proxy_pass http://backend-dev/actuator/health;
  }
}
EOF

# nginx-proxy-dev 재시작
echo "[INFO] nginx-proxy-dev 재시작 중..."
if docker ps -a --format '{{.Names}}' | grep -q '^nginx-proxy-dev$'; then
  docker restart nginx-proxy-dev || {
    echo "[ERROR] nginx-proxy-dev 재시작 실패"
    exit 1
  }
else
  docker compose -f "$NEXT_COMPOSE" up -d nginx || {
    echo "[ERROR] nginx-proxy-dev 실행 실패"
    exit 1
  }
fi

# Prometheus 설정 갱신
PROM_TEMPLATE_PATH="../../../infrastructure/prometheus/prometheus-dev.template.yml"
PROM_CONFIG_PATH="../../../infrastructure/prometheus/prometheus-dev.yml"

echo "[INFO] Prometheus 설정 갱신 → 대상: $BACKEND_NAME"
export BACKEND_SERVICE_HOST="$BACKEND_NAME"
envsubst < "$PROM_TEMPLATE_PATH" > "$PROM_CONFIG_PATH"

echo "[INFO] Prometheus 컨테이너 재시작..."
if docker ps -a --format '{{.Names}}' | grep -q '^prometheus-dev$'; then
  docker restart prometheus-dev || {
    echo "[ERROR] Prometheus-dev 재시작 실패"
    exit 1
  }
else
  docker compose -f ../../../infrastructure/prometheus/docker-compose-dev.yml up -d prometheus || {
    echo "[ERROR] Prometheus-dev 실행 실패"
    exit 1
  }
fi

# 이전 컨테이너 종료
echo "[INFO] 이전 컨테이너 종료 중: backend-${CURRENT}-dev"
if docker ps --format '{{.Names}}' | grep -q "backend-${CURRENT}-dev"; then
  docker stop "backend-${CURRENT}-dev" || true
fi
docker rm -f "backend-${CURRENT}-dev" || echo "[WARN] 제거 실패 또는 이미 없음"

# 상태 갱신
echo "$NEXT" > "$CURRENT_COLOR_FILE"

echo "========================================"
echo "[DONE] 개발 서버 무중단 배포 완료: [$NEXT]"
echo "========================================"
