#!/bin/bash
set -euo pipefail

# ===== 공통 유틸 =====
log(){ echo "[$(date -u +%FT%TZ)] $*"; }

# ===== 경로 설정 =====
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
STATE_DIR="/home/ubuntu/color-config"

log "=== Dataracy 서비스 상태 확인 ==="

# 현재 활성 색상 확인
log ""
log "=== 현재 활성 색상 ==="
for env in dev prod; do
  state_file="$STATE_DIR/current_color_$env"
  if [ -f "$state_file" ]; then
    color=$(tr -d '[:space:]' < "$state_file")
    log "$env: $color"
  else
    log "$env: 파일 없음 (초기 상태)"
  fi
done

# 컨테이너 상태
log ""
log "=== 컨테이너 상태 ==="
for container in backend-blue backend-green backend-prod-blue backend-prod-green nginx-proxy; do
  if docker ps --format '{{.Names}}' | grep -q "^${container}$"; then
    status=$(docker inspect --format='{{json .State.Health.Status}}' "$container" 2>/dev/null || echo "no-healthcheck")
    log "$container: 실행 중 (Health: $status)"
  else
    log "$container: 중지됨"
  fi
done

# Nginx 상태
log ""
log "=== Nginx 상태 ==="
if docker ps --format '{{.Names}}' | grep -q "^nginx-proxy$"; then
  log "nginx-proxy: 실행 중"
  
  # Nginx 설정 검증
  if docker exec nginx-proxy nginx -t 2>/dev/null; then
    log "Nginx 설정: 유효함"
  else
    log "Nginx 설정: 오류 있음"
  fi
else
  log "nginx-proxy: 중지됨"
fi

# 네트워크 상태
log ""
log "=== 네트워크 상태 ==="
for network in dataracy-network-dev dataracy-network-prod; do
  if docker network ls --format '{{.Name}}' | grep -q "^${network}$"; then
    log "$network: 존재함"
  else
    log "$network: 없음"
  fi
done

# 포트 사용 현황
log ""
log "=== 포트 사용 현황 ==="
for port in 80 443; do
  if netstat -tlnp 2>/dev/null | grep -q ":$port "; then
    log "포트 $port: 사용 중"
  else
    log "포트 $port: 사용 안함"
  fi
done

# 서비스 접근 테스트
log ""
log "=== 서비스 접근 테스트 ==="
for url in "https://dev.api.dataracy.co.kr/actuator/health" "https://api.dataracy.co.kr/actuator/health"; do
  if curl -s -f --max-time 10 "$url" >/dev/null 2>&1; then
    log "$url: 접근 가능"
  else
    log "$url: 접근 불가"
  fi
done

log ""
log "=== 상태 확인 완료 ==="
