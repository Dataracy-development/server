#!/bin/bash
set -Eeuo pipefail

# ===== 공통 유틸 =====
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

# 동시 실행 방지 락(PROD 전용)
exec 9>/tmp/dataracy.prod.deploy.lock
flock -n 9 || fail "다른 배포 중"
trap 'fail "스크립트 오류(line:$LINENO)"' ERR

# ===== 경로 설정 =====
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"
NGINX_DIR="$SCRIPT_DIR/../nginx"              # prod nginx 경로
STATE_DIR="/home/ubuntu/color-config"
CUR_FILE="$STATE_DIR/current_color_prod"

# Nginx (통합)
NGINX_COMPOSE="$SCRIPT_DIR/../../../nginx/docker-compose-nginx.yml"
NGINX_SVC_NAME="nginx-proxy"

# ===== 현재 색상 =====
mkdir -p "$STATE_DIR"
[[ -f "$CUR_FILE" ]] || echo "blue" > "$CUR_FILE"
CUR="$(tr -d '[:space:]' < "$CUR_FILE")"
[[ "$CUR" =~ ^(blue|green)$ ]] || fail "current_color_prod 값 오류:$CUR"

NEXT=$([[ "$CUR" == "blue" ]] && echo green || echo blue)
NEXT_COMPOSE="$DOCKER_DIR/docker-compose-${NEXT}-prod.yml"
BACKEND="backend-prod-${NEXT}"

log "[DEPLOY] PROD $CUR → $NEXT"

# ===== 새 컨테이너 실행 =====
docker compose -f "$NEXT_COMPOSE" up -d --pull always

# Health check
s="null"
for i in {1..20}; do
  s="$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND" 2>/dev/null || echo null)"
  [[ "$s" == "\"healthy\"" ]] && { log "$BACKEND healthy"; break; } || { log "  [$i/20] $s"; sleep 5; }
done
[[ "$s" == "\"healthy\"" ]] || fail "$BACKEND healthy 실패"

# ===== prod upstream 설정만 업데이트 =====
UPSTREAM="$NGINX_DIR/upstream-blue-green-prod.conf"

# 기존 설정 파일을 백업
cp "$UPSTREAM" "$UPSTREAM.backup" || true

# upstream 설정만 업데이트 (backend_prod 서버 변경)
sed -i "s|server backend-prod-[a-z]*:8080|server $BACKEND:8080|g" "$UPSTREAM"

# ===== Nginx Reload (검증 포함) =====
docker ps --format '{{.Names}}' | grep -q "^${NGINX_SVC_NAME}$" || fail "nginx-proxy 컨테이너가 없음"
docker exec "$NGINX_SVC_NAME" nginx -t || fail "Nginx 설정 검사 실패(prod)"
docker exec "$NGINX_SVC_NAME" nginx -s reload || fail "Nginx reload 실패(prod)"

# ===== 이전 컨테이너 정리 =====
PREV="backend-prod-$CUR"
docker stop "$PREV" || true
docker rm -f "$PREV" || true

echo "$NEXT" > "$CUR_FILE"
log "[DONE] PROD 활성: $NEXT"
