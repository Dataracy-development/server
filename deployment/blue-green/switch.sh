#!/bin/bash

set -e  # 오류 발생 시 스크립트 즉시 종료

echo "========================================"
echo "[DEPLOY] Blue/Green 무중단 배포 시작"
echo "========================================"

# 현재 색상 읽기 (없으면 기본 blue)
CURRENT=$(cat ./current_color 2>/dev/null || echo "blue")

# 다음 색상 결정
if [ "$CURRENT" == "blue" ]; then
  NEXT="green"
else
  NEXT="blue"
fi
CURRENT_COMPOSE="../docker/docker-compose-${NEXT}.yml"
BACKEND_NAME="backend-${NEXT}"

echo "[INFO] 현재 배포 중인 컨테이너: $CURRENT"
echo "[INFO] 새로운 컨테이너로 전환합니다: $NEXT"

# ✅ 새 컨테이너 실행
docker compose -f "$CURRENT_COMPOSE" up -d --build

# ✅ Health Check 대기
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

# ✅ 실패 시 롤백
if [ "$STATUS" != "\"healthy\"" ]; then
  echo "[ERROR] $BACKEND_NAME 컨테이너가 정상 상태가 아닙니다. 배포를 중단합니다."
  docker rm -f "$BACKEND_NAME" || true
  exit 1
fi

# ✅ Nginx 설정 파일 업데이트
NGINX_CONF_PATH="../nginx/upstream-blue-green.conf"
echo "[INFO] Nginx upstream 설정 갱신: $BACKEND_NAME → localhost:8080"
cat > "$NGINX_CONF_PATH" <<EOF
upstream backend {
  server $BACKEND_NAME:8080;
}
server {
  listen 80;
  server_name dataracy.co.kr;

  location / {
    proxy_pass http://backend;
    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
  }

  location /actuator/health {
    proxy_pass http://backend/actuator/health;
  }
}
EOF

# ✅ nginx-proxy 재시작
echo "[INFO] nginx-proxy 재시작 중..."
if docker ps -a --format '{{.Names}}' | grep -q '^nginx-proxy$'; then
  docker restart nginx-proxy || {
    echo "[ERROR] nginx-proxy 재시작 실패 → 재생성 시도"
    docker rm -f nginx-proxy || true
    docker compose -f "$CURRENT_COMPOSE" up -d nginx
  }
else
  docker compose -f "$CURRENT_COMPOSE" up -d nginx
fi

# ✅ 이전 컨테이너 종료
echo "[INFO] 이전 컨테이너 종료 중: backend-${CURRENT}"
docker rm -f "backend-${CURRENT}" || echo "[WARN] backend-${CURRENT} 제거 실패 또는 이미 없음"

# ✅ 상태 기록
echo "$NEXT" > ./current_color

echo "========================================"
echo "[DONE] 무중단 배포 완료! 현재 활성 인스턴스는 [$NEXT]"
echo "========================================"
