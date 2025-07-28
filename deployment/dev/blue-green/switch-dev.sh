#!/bin/bash

set -euo pipefail

echo "========================================"
echo "[DEV DEPLOY] Blue/Green 무중단 배포 시작"
echo "========================================"

DEPLOY_STATE_DIR="/home/ubuntu/color-config"
CURRENT_COLOR_FILE="$DEPLOY_STATE_DIR/current_color_dev"

mkdir -p "$DEPLOY_STATE_DIR"
if [ ! -f "$CURRENT_COLOR_FILE" ]; then
  echo "blue" > "$CURRENT_COLOR_FILE"
fi

CURRENT=$(cat "$CURRENT_COLOR_FILE")
NEXT=$([[ "$CURRENT" == "blue" ]] && echo "green" || echo "blue")
BACKEND_NAME="backend-${NEXT}-dev"
NEXT_COMPOSE="../docker/docker-compose-${NEXT}-dev.yml"

echo "[INFO] 현재 배포 중인 컨테이너: $CURRENT"
echo "[INFO] 새로 배포할 색상: $NEXT"

# Docker Hub에서 latest pull 후 태그 변경
echo "[INFO] Docker 이미지 pull 및 retag: latest → $NEXT"
docker pull juuuunny/backend:latest
docker tag juuuunny/backend:latest juuuunny/backend:$NEXT

# 새 컨테이너 실행
docker-compose -f "$NEXT_COMPOSE" up -d

# Health Check
echo "[INFO] Health Check 시작: $BACKEND_NAME ..."
for i in {1..20}; do
  STATUS=$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND_NAME" 2>/dev/null || echo "null")
  if [ "$STATUS" == "\"healthy\"" ]; then
    echo "[SUCCESS] $BACKEND_NAME 컨테이너가 정상입니다."
    break
  else
    echo "  [$i/20] 대기 중... (상태: $STATUS)"
    sleep 5
  fi
done

# 실패 시 롤백
if [ "$STATUS" != "\"healthy\"" ]; then
  echo "[ERROR] $BACKEND_NAME 실행 실패 → 롤백 시작"
  docker rm -f "$BACKEND_NAME" || true
  echo "[INFO] 롤백 완료: 기존 컨테이너 유지 ($CURRENT)"
  exit 1
fi

# NGINX 업스트림 교체
NGINX_CONF_PATH="../nginx/upstream-blue-green-dev.conf"
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

echo "[INFO] NGINX 재시작 중..."
docker restart nginx-proxy-dev || {
  echo "[ERROR] nginx-proxy-dev 재시작 실패"
  exit 1
}

# Prometheus 설정 교체
PROM_TEMPLATE_PATH="../../../infrastructure/prometheus/prometheus-dev.template.yml"
PROM_CONFIG_PATH="../../../infrastructure/prometheus/prometheus-dev.yml"

echo "[INFO] Prometheus 설정 갱신: $BACKEND_NAME"
export BACKEND_SERVICE_HOST="$BACKEND_NAME"
envsubst < "$PROM_TEMPLATE_PATH" > "$PROM_CONFIG_PATH"

echo "[INFO] Prometheus 재시작 중..."
docker restart prometheus-dev || {
  echo "[ERROR] Prometheus 재시작 실패"
  exit 1
}

# 이전 컨테이너 종료
OLD_BACKEND="backend-${CURRENT}-dev"
echo "[INFO] 이전 컨테이너 종료: $OLD_BACKEND"
docker stop "$OLD_BACKEND" || true
docker rm -f "$OLD_BACKEND" || echo "[WARN] 제거 실패 또는 이미 없음"

# 상태 갱신
echo "$NEXT" > "$CURRENT_COLOR_FILE"

echo "========================================"
echo "[DONE] 무중단 배포 완료: [$NEXT]"
echo "========================================"
