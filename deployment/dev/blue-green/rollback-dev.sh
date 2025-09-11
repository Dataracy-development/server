#!/bin/bash
set -Eeuo pipefail

# ===== 공통 유틸 =====
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

# ===== 경로 설정 =====
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"
NGINX_DIR="$SCRIPT_DIR/../nginx"
STATE_DIR="/home/ubuntu/color-config"
CUR_FILE="$STATE_DIR/current_color_dev"

# ===== 현재 색상 확인 =====
if [ ! -f "$CUR_FILE" ]; then
  fail "현재 색상 파일이 없습니다: $CUR_FILE"
fi

CUR="$(tr -d '[:space:]' < "$CUR_FILE")"
[[ "$CUR" =~ ^(blue|green)$ ]] || fail "current_color_dev 값 오류: $CUR"

# 이전 색상으로 롤백
PREV=$([[ "$CUR" == "blue" ]] && echo green || echo blue)
PREV_COMPOSE="$DOCKER_DIR/docker-compose-${PREV}-dev.yml"
BACKEND_NAME="backend-${PREV}"

log "== DEV ROLLBACK: $CUR → $PREV =="

# ===== 이전 컨테이너가 실행 중인지 확인 =====
if ! docker ps --format '{{.Names}}' | grep -q "^${BACKEND_NAME}$"; then
  log "이전 컨테이너가 실행 중이 아닙니다. 시작 중..."
  docker compose -f "$PREV_COMPOSE" up -d --pull always
  
  # Health check
  s="null"
  for i in {1..20}; do
    s="$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND_NAME" 2>/dev/null || echo null)"
    [[ "$s" == "\"healthy\"" ]] && { log "$BACKEND_NAME healthy"; break; } || { log "  [$i/20] $s"; sleep 5; }
  done
  [[ "$s" == "\"healthy\"" ]] || fail "$BACKEND_NAME healthy 실패"
fi

# ===== dev upstream 설정 업데이트 =====
UPSTREAM_DEST="$NGINX_DIR/upstream-blue-green-dev.conf"

# 기존 설정 파일을 백업
cp "$UPSTREAM_DEST" "$UPSTREAM_DEST.backup" || true

# upstream 설정 업데이트
sed -i "s|server backend-[a-z]*:8080|server $BACKEND_NAME:8080|g" "$UPSTREAM_DEST"

# ===== Nginx Reload =====
NGINX_SVC_NAME="nginx-proxy"
docker ps --format '{{.Names}}' | grep -q "^${NGINX_SVC_NAME}$" || fail "nginx-proxy 컨테이너가 없음"
docker exec "$NGINX_SVC_NAME" nginx -t || fail "Nginx 설정 검사 실패(dev)"
docker exec "$NGINX_SVC_NAME" nginx -s reload || fail "Nginx reload 실패(dev)"

# ===== 현재 컨테이너 정리 =====
CURRENT="backend-${CUR}"
docker stop "$CURRENT" || true
docker rm -f "$CURRENT" || true

echo "$PREV" > "$CUR_FILE"
log "[DONE] DEV 롤백 완료: $PREV"
