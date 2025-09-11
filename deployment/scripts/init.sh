#!/bin/bash
set -euo pipefail

# ===== 공통 유틸 =====
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

log "=== Dataracy 초기 설정 시작 ==="

# ===== Docker 네트워크 생성 =====
log "Docker 네트워크 생성 중..."
for network in dataracy-network-dev dataracy-network-prod; do
  if ! docker network ls --format '{{.Name}}' | grep -q "^${network}$"; then
    log "네트워크 생성: $network"
    docker network create "$network"
  else
    log "네트워크 이미 존재: $network"
  fi
done

# 운영 환경 네트워크가 없어도 Nginx가 정상 작동하도록 설정
log "네트워크 설정 확인 중..."
if ! docker network ls --format '{{.Name}}' | grep -q "^dataracy-network-prod$"; then
  log "운영 환경 네트워크가 없습니다. Nginx는 개발 환경만 사용합니다."
fi

# ===== 상태 디렉터리 생성 =====
log "상태 디렉터리 생성 중..."
STATE_DIR="/home/ubuntu/color-config"
mkdir -p "$STATE_DIR"

# ===== 초기 색상 설정 =====
for env in dev prod; do
  state_file="$STATE_DIR/current_color_$env"
  if [ ! -f "$state_file" ]; then
    log "초기 색상 설정: $env = blue"
    echo "blue" > "$state_file"
  else
    current=$(tr -d '[:space:]' < "$state_file")
    log "현재 색상 유지: $env = $current"
  fi
done

# ===== Nginx 컨테이너 시작 =====
log "Nginx 컨테이너 시작 중..."
NGINX_COMPOSE="$SCRIPT_DIR/../nginx/docker-compose-nginx.yml"
if [ -f "$NGINX_COMPOSE" ]; then
  docker compose -f "$NGINX_COMPOSE" up -d
  log "Nginx 컨테이너 시작 완료"
else
  fail "Nginx Compose 파일을 찾을 수 없습니다: $NGINX_COMPOSE"
fi

# ===== 스크립트 실행 권한 부여 =====
log "스크립트 실행 권한 부여 중..."
chmod +x "$SCRIPT_DIR"/*.sh
chmod +x "$SCRIPT_DIR/../dev/blue-green"/*.sh
chmod +x "$SCRIPT_DIR/../prod/blue-green"/*.sh
chmod +x "$SCRIPT_DIR/../dev/script"/*.sh
chmod +x "$SCRIPT_DIR/../prod/script"/*.sh

# ===== 초기 백엔드 컨테이너 시작 =====
for env in dev prod; do
  log "$env 환경 초기 컨테이너 시작 중..."
  BLUE_COMPOSE="$SCRIPT_DIR/../${env}/docker/docker-compose-blue-${env}.yml"
  if [ -f "$BLUE_COMPOSE" ]; then
    docker compose -f "$BLUE_COMPOSE" up -d
    log "$env blue 컨테이너 시작 완료"
  else
    log "경고: $BLUE_COMPOSE 파일을 찾을 수 없습니다."
  fi
done

# ===== 상태 확인 =====
log "초기 설정 완료. 상태 확인 중..."
if [ -f "$SCRIPT_DIR/status.sh" ]; then
  chmod +x "$SCRIPT_DIR/status.sh"
  "$SCRIPT_DIR/status.sh"
fi

log "=== 초기 설정 완료 ==="
