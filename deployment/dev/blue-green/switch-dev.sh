#!/bin/bash
# switch-dev.sh — Blue/Green 무중단 배포 (내 구현 의도 반영: upstream 교체 후 NGINX 컨테이너 ‘재시작’로 확실 반영)
# - 변경점: 원자적 파일 교체(tmp→mv), 동시 실행 잠금, 로그 보강, 실패 시 직관적 종료
# - 주의: NGINX는 reload가 아니라 ‘재시작’한다(캐시/잔존 상태 정리 목적). 필요 시 아래 ALWAYS_RELOAD=false 변수만 true로 바꾸면 reload 경로 지원.

set -Eeuo pipefail

#######################################
# 공통 유틸
#######################################
log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*"; }
fail() { log "[ERROR] $*"; exit 1; }

# 동시 배포 방지 잠금
exec 9>/tmp/dataracy.deploy.lock
if ! flock -n 9; then
  fail "다른 배포가 진행 중이다. 잠금 해제 후 다시 시도한다."
fi

trap 'fail "스크립트 오류로 중단됨(라인:$LINENO)"' ERR

#######################################
# 경로/변수
#######################################
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
BLUE_GREEN_DIR="$SCRIPT_DIR"                # deployment/dev/blue-green 기준
DOCKER_DIR="$SCRIPT_DIR/../docker"
NGINX_DIR="$SCRIPT_DIR/../nginx"

DEPLOY_STATE_DIR="/home/ubuntu/color-config"
CURRENT_COLOR_FILE="$DEPLOY_STATE_DIR/current_color_dev"

# 운영 의도: 기본은 NGINX 재시작(캐시/상태 완전 초기화). reload 원하면 true로 변경.
ALWAYS_RELOAD=false

# NGINX compose 파일/서비스명
NGINX_COMPOSE="$DOCKER_DIR/docker-compose-nginx-dev.yml"
NGINX_SVC_NAME="nginx-proxy-dev"

#######################################
# 현재/다음 색상
#######################################
mkdir -p "$DEPLOY_STATE_DIR"
if [ ! -f "$CURRENT_COLOR_FILE" ]; then
  echo "blue" > "$CURRENT_COLOR_FILE"
fi

CURRENT="$(cat "$CURRENT_COLOR_FILE" | tr -d '[:space:]')"
if [[ "$CURRENT" != "blue" && "$CURRENT" != "green" ]]; then
  fail "current_color_dev 값이 유효하지 않다: $CURRENT"
fi

NEXT=$([[ "$CURRENT" == "blue" ]] && echo "green" || echo "blue")
NEXT_COMPOSE="$DOCKER_DIR/docker-compose-${NEXT}-dev.yml"
BACKEND_NAME="backend-${NEXT}"

log "========================================"
log "[DEPLOY] Blue/Green 무중단 배포 시작 — 현재:$CURRENT → 다음:$NEXT"
log "========================================"

#######################################
# 1) 비활성 색상 컨테이너 기동(항상 최신 pull)
#######################################
if [ ! -f "$NEXT_COMPOSE" ]; then
  fail "Compose 파일을 찾을 수 없다: $NEXT_COMPOSE"
fi

log "[INFO] 비활성 컨테이너 기동: $BACKEND_NAME (compose: $(basename "$NEXT_COMPOSE"))"
docker compose -f "$NEXT_COMPOSE" up -d --pull always

#######################################
# 2) 컨테이너 HealthCheck 대기
#######################################
log "[INFO] Health Check 대기: $BACKEND_NAME ..."
STATUS="starting"
for i in {1..20}; do
  STATUS="$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND_NAME" 2>/dev/null || echo "null")"
  if [ "$STATUS" == "\"healthy\"" ]; then
    log "[SUCCESS] $BACKEND_NAME 가 healthy 상태다."
    break
  else
    log "  [$i/20] 아직 준비되지 않음... (상태: $STATUS)"
    sleep 5
  fi
done
if [ "$STATUS" != "\"healthy\"" ]; then
  fail "$BACKEND_NAME 컨테이너가 healthy 상태가 아니다. 배포 중단."
fi

#######################################
# 3) NGINX upstream 설정 — 원자적 교체(tmp→mv)
#######################################
UPSTREAM_DEST="$NGINX_DIR/upstream-blue-green-dev.conf"
UPSTREAM_TMP="$NGINX_DIR/upstream-blue-green-dev.conf.tmp"

log "[INFO] NGINX upstream 갱신(원자적 교체): $BACKEND_NAME:8080 → $UPSTREAM_DEST"
cat > "$UPSTREAM_TMP" <<'EOF'
# 이 파일은 switch-dev.sh에 의해 자동 생성된다.
# upstream 대상은 아래 server 라인만 변경된다.
upstream backend {
  server REPLACE_BACKEND_NAME:8080;
}

server {
  listen 80;
  server_name dataracy.store;

  client_max_body_size 50m;
  client_body_timeout 60s;

  # 공통 헤더
  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
  proxy_http_version 1.1;
  proxy_set_header Upgrade $http_upgrade;
  proxy_set_header Connection "upgrade";
  proxy_set_header Cookie $http_cookie;

  # API
  location /api {
    proxy_pass http://backend;
    proxy_request_buffering off;
  }

  # Swagger/UI/Docs: 캐시 무효화
  location /swagger-ui/ {
    proxy_pass http://backend/swagger-ui/;
    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always;
    add_header Pragma "no-cache" always;
    expires -1;
    etag off;
  }
  location /v3/api-docs {
    proxy_pass http://backend/v3/api-docs;
    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always;
    add_header Pragma "no-cache" always;
    expires -1;
    etag off;
  }
  location /swagger-config {
    proxy_pass http://backend/swagger-config;
    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always;
    add_header Pragma "no-cache" always;
    expires -1;
    etag off;
  }
  location /webjars/ {
    proxy_pass http://backend/webjars/;
    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always;
    add_header Pragma "no-cache" always;
    expires -1;
    etag off;
  }

  # 헬스
  location /actuator/health {
    proxy_pass http://backend/actuator/health;
  }

  # 기타
  location / {
    proxy_pass http://backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_set_header Cookie $http_cookie;
  }
}
EOF

# 실제 backend 이름으로 치환
sed -i "s|REPLACE_BACKEND_NAME|$BACKEND_NAME|g" "$UPSTREAM_TMP"
mv -f "$UPSTREAM_TMP" "$UPSTREAM_DEST"

#######################################
# 4) NGINX 설정 반영 — 기본: 재시작(확실 반영), 선택: reload
#######################################
if [ "$ALWAYS_RELOAD" = true ]; then
  log "[INFO] NGINX 무중단 재적용(nginx -t && reload) 시도"
  docker ps --format '{{.Names}}' | grep -q "^${NGINX_SVC_NAME}$" || \
    fail "NGINX 컨테이너(${NGINX_SVC_NAME})가 실행 중이 아니다."
  docker exec "$NGINX_SVC_NAME" nginx -t
  docker exec "$NGINX_SVC_NAME" nginx -s reload
  log "[SUCCESS] NGINX reload 완료"
else
  log "[INFO] NGINX 컨테이너 완전 재시작(캐시/잔존 상태 정리 목적)"
  docker compose -f "$NGINX_COMPOSE" down -v
  docker compose -f "$NGINX_COMPOSE" up -d --build
  # 간단 대기(필요 시 healthcheck 추가 고려)
  sleep 2
  log "[SUCCESS] NGINX 재시작 및 설정 반영 완료"
fi

#######################################
# 5) 이전 컨테이너 종료/삭제 (서비스 전환 완료 후)
#######################################
PREV_NAME="backend-${CURRENT}"
log "[INFO] 이전 컨테이너 정리: $PREV_NAME"
if docker ps --format '{{.Names}}' | grep -q "^${PREV_NAME}$"; then
  docker stop "$PREV_NAME" || true
fi
docker rm -f "$PREV_NAME" || log "[WARN] $PREV_NAME 제거 실패 또는 이미 없음"

#######################################
# 6) 상태 파일 갱신
#######################################
echo "$NEXT" > "$CURRENT_COLOR_FILE"
log "[DONE] 배포 완료 — 현재 활성 인스턴스: [$NEXT]"
log "========================================"
