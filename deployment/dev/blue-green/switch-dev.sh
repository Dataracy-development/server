#!/bin/bash

set -e

echo "========================================"
echo "[DEPLOY] Blue/Green 무중단 배포 시작"
echo "========================================"

DEPLOY_STATE_DIR="/home/ubuntu/color-config"
CURRENT_COLOR_FILE="$DEPLOY_STATE_DIR/current_color_dev"

mkdir -p "$DEPLOY_STATE_DIR"
if [ ! -f "$CURRENT_COLOR_FILE" ]; then
  echo "blue" > "$CURRENT_COLOR_FILE"
fi

CURRENT=$(cat "$CURRENT_COLOR_FILE")
NEXT=$([[ "$CURRENT" == "blue" ]] && echo "green" || echo "blue")

NEXT_COMPOSE="../docker/docker-compose-${NEXT}-dev.yml"
BACKEND_NAME="backend-${NEXT}"

echo "[INFO] 현재 배포 중인 컨테이너: $CURRENT"
echo "[INFO] 새로운 컨테이너로 전환합니다: $NEXT"

docker compose -f "$NEXT_COMPOSE" up -d --build

echo "[INFO] Health Check 진행 중: $BACKEND_NAME ..."
for i in {1..20}; do
  STATUS=$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND_NAME" 2>/dev/null || echo "null")
  if [ "$STATUS" == "\"healthy\"" ]; then
    echo "[SUCCESS] $BACKEND_NAME 컨테이너가 정상적으로 실행되었습니다."
    break
  else
    echo "  [$i/20] 아직 준비되지 않음... (상태: $STATUS)"
    sleep 5
  fi
done

if [ "$STATUS" != "\"healthy\"" ]; then
  echo "[ERROR] $BACKEND_NAME 컨테이너가 정상 상태가 아닙니다. 배포 중단"
#  docker rm -f "$BACKEND_NAME" || true
  exit 1
fi

NGINX_UPSTREAM="../nginx/upstream-blue-green-dev.conf"
echo "[INFO] Nginx upstream 설정 갱신: $BACKEND_NAME → localhost:8080"

cat > "$NGINX_UPSTREAM" <<EOF
upstream backend {
  server $BACKEND_NAME:8080;
}
server {
  listen 80;
  server_name dataracy.store;

  location / {
    proxy_pass http://backend;

    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;

    proxy_http_version 1.1;
    proxy_set_header Upgrade \$http_upgrade;
    proxy_set_header Connection "upgrade";

    proxy_set_header Cookie \$http_cookie;
  }

  location /swagger-ui/ {
      proxy_pass http://backend/swagger-ui/;
    }

    location /v3/api-docs {
      proxy_pass http://backend/v3/api-docs;
    }

  location /actuator/health {
    proxy_pass http://backend/actuator/health;
  }
}
EOF

echo "[INFO] Nginx 설정 반영 중 (nginx-proxy-dev)"
docker restart nginx-proxy-dev

# 이전 백엔드 종료
echo "[INFO] 이전 컨테이너 종료 중: backend-${CURRENT}"
if docker ps --format '{{.Names}}' | grep -q "backend-${CURRENT}"; then
  docker stop "backend-${CURRENT}" || true
fi
docker rm -f "backend-${CURRENT}" || echo "[WARN] backend-${CURRENT} 제거 실패 또는 없음"

echo "$NEXT" > "$CURRENT_COLOR_FILE"

echo "========================================"
echo "[DONE] 무중단 배포 완료! 현재 활성 인스턴스는 [$NEXT]"
echo "========================================"
