#!/bin/bash
# switch-dev.sh — Blue/Green 무중단 배포 (upstream 교체 후 NGINX 재시작 기본)
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
BLUE_GREEN_DIR="$SCRIPT_DIR"
DOCKER_DIR="$SCRIPT_DIR/../docker"
NGINX_DIR="$SCRIPT_DIR/../nginx"

DEPLOY_STATE_DIR="/home/ubuntu/color-config"
CURRENT_COLOR_FILE="$DEPLOY_STATE_DIR/current_color_dev"

# 운영 의도: 기본은 NGINX 재시작(캐시/상태 완전 초기화). reload 원하면 true.
ALWAYS_RELOAD=false

# NGINX compose 파일/서비스명 (개발 환경 NGINX 사용)
NGINX_COMPOSE="../docker/docker-compose-nginx-dev.yml"
NGINX_SVC_NAME="nginx-proxy-dev"

# ★ Kibana 업스트림(환경변수로 오버라이드 가능)
# - NGINX가 같은 docker 네트워크면: kibana-dev:5601
# - 호스트에서 직접 프록시면: 127.0.0.1:5601
KIBANA_UPSTREAM="${KIBANA_UPSTREAM:-kibana-dev:5601}"

#######################################
# 현재/다음 색상
#######################################
mkdir -p "$DEPLOY_STATE_DIR"
if [ ! -f "$CURRENT_COLOR_FILE" ]; then
  echo "blue" > "$CURRENT_COLOR_FILE"
fi

CURRENT="$(tr -d '[:space:]' < "$CURRENT_COLOR_FILE")"
if [[ "$CURRENT" != "blue" && "$CURRENT" != "green" ]]; then
  fail "current_color_dev 값이 유효하지 않다: $CURRENT"
fi

NEXT=$([[ "$CURRENT" == "blue" ]] && echo "green" || echo "blue")
NEXT_COMPOSE="$DOCKER_DIR/docker-compose-${NEXT}-dev.yml"
BACKEND_NAME="backend-dev-${NEXT}"

log "========================================"
log "[DEPLOY] Blue/Green 무중단 배포 시작 — 현재:$CURRENT → 다음:$NEXT"
log "========================================"

#######################################
# 1) 비활성 색상 컨테이너 기동
#######################################
if [ ! -f "$NEXT_COMPOSE" ]; then
  fail "Compose 파일을 찾을 수 없다: $NEXT_COMPOSE"
fi

log "[INFO] 비활성 컨테이너 기동: $BACKEND_NAME (compose: $(basename "$NEXT_COMPOSE"))"
docker compose -f "$NEXT_COMPOSE" up -d --pull always

#######################################
# 2) HealthCheck 대기
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
UPSTREAM_DEST="../nginx/upstream-blue-green-dev.conf"
UPSTREAM_TMP="../nginx/upstream-blue-green-dev.conf.tmp"

log "[INFO] NGINX upstream 갱신(원자적 교체): $BACKEND_NAME:8080, Kibana:$KIBANA_UPSTREAM → $UPSTREAM_DEST"
cat > "$UPSTREAM_TMP" <<'EOF'
# 이 파일은 switch-dev.sh에 의해 자동 생성된다.
# backend와 kibana upstream 대상은 아래 server 라인만 변경된다.

upstream backend {
  server REPLACE_BACKEND_NAME:8080;
}

# ★ Kibana 업스트림(경로 프록시용)
upstream kibana_dev {
  server REPLACE_KIBANA_UPSTREAM;
}


# HTTP server for dev-api.dataracy.co.kr (개발 환경은 HTTP/HTTPS 모두 지원)
server {
  listen 80;
  server_name dev-api.dataracy.co.kr;
  
  client_max_body_size 50m;
  client_body_timeout 120s;

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
    proxy_send_timeout 300s;
    proxy_read_timeout 300s;
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

  # ★ Kibana (경로 /kibana)
  location /kibana/ {
    proxy_pass http://kibana_dev/kibana/;     # 뒤 슬래시 필수
    proxy_read_timeout 600s;
    proxy_send_timeout 600s;

    # WebSocket
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";

    # 프록시 뒤 basePath 사용 시 권장 헤더
    proxy_set_header X-Forwarded-Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Prefix /kibana;
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


# HTTPS server for dev-api.dataracy.co.kr
server {
  listen 443 ssl;
  http2 on;
  server_name dev-api.dataracy.co.kr;

  # ★ 추가: Cloudflare Origin Certificate 경로
  ssl_certificate     /etc/nginx/ssl/cloudflare/origin.crt;
  ssl_certificate_key /etc/nginx/ssl/cloudflare/origin.key;

  # TLS 보안 옵션
  ssl_protocols TLSv1.2 TLSv1.3;
  ssl_prefer_server_ciphers on;

  client_max_body_size 50m;
  client_body_timeout 120s;

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
    proxy_send_timeout 300s;
    proxy_read_timeout 300s;
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

  # ★ Kibana (경로 /kibana)
  location /kibana/ {
    proxy_pass http://kibana_dev/kibana/;     # 뒤 슬래시 필수
    proxy_read_timeout 600s;
    proxy_send_timeout 600s;

    # WebSocket
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";

    # 프록시 뒤 basePath 사용 시 권장 헤더
    proxy_set_header X-Forwarded-Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Prefix /kibana;
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

# 실제 backend/kibana 대상 치환
sed -i "s|REPLACE_BACKEND_NAME|$BACKEND_NAME|g" "$UPSTREAM_TMP"
sed -i "s|REPLACE_KIBANA_UPSTREAM|$KIBANA_UPSTREAM|g" "$UPSTREAM_TMP"
mv -f "$UPSTREAM_TMP" "$UPSTREAM_DEST"

#######################################
# 4) NGINX 설정 반영 — 재시작(기본) 또는 reload
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
  sleep 2
  log "[SUCCESS] NGINX 재시작 및 설정 반영 완료"
fi

#######################################
# 5) 이전 컨테이너 종료/삭제
#######################################
PREV_NAME="backend-dev-${CURRENT}"
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