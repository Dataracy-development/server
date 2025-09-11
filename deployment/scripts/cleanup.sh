#!/bin/bash
set -euo pipefail

# ===== 공통 유틸 =====
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

# ===== 사용법 =====
if [ $# -ne 1 ]; then
  echo "사용법: $0 <dev|prod|all>"
  echo "예시: $0 dev"
  exit 1
fi

TARGET="$1"
if [[ "$TARGET" != "dev" && "$TARGET" != "prod" && "$TARGET" != "all" ]]; then
  fail "대상은 dev, prod, 또는 all이어야 합니다."
fi

log "=== Dataracy 정리 시작: $TARGET ==="

# ===== 컨테이너 정리 =====
if [[ "$TARGET" == "dev" || "$TARGET" == "all" ]]; then
  log "DEV 컨테이너 정리 중..."
  for container in backend-blue backend-green; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${container}$"; then
      log "컨테이너 중지 및 제거: $container"
      docker stop "$container" 2>/dev/null || true
      docker rm -f "$container" 2>/dev/null || true
    fi
  done
fi

if [[ "$TARGET" == "prod" || "$TARGET" == "all" ]]; then
  log "PROD 컨테이너 정리 중..."
  for container in backend-prod-blue backend-prod-green; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${container}$"; then
      log "컨테이너 중지 및 제거: $container"
      docker stop "$container" 2>/dev/null || true
      docker rm -f "$container" 2>/dev/null || true
    fi
  done
fi

if [[ "$TARGET" == "all" ]]; then
  log "Nginx 컨테이너 정리 중..."
  if docker ps -a --format '{{.Names}}' | grep -q "^nginx-proxy$"; then
    log "Nginx 컨테이너 중지 및 제거"
    docker stop nginx-proxy 2>/dev/null || true
    docker rm -f nginx-proxy 2>/dev/null || true
  fi
fi

# ===== 네트워크 정리 =====
if [[ "$TARGET" == "all" ]]; then
  log "Docker 네트워크 정리 중..."
  for network in dataracy-network-dev dataracy-network-prod; do
    if docker network ls --format '{{.Name}}' | grep -q "^${network}$"; then
      log "네트워크 제거: $network"
      docker network rm "$network" 2>/dev/null || true
    fi
  done
fi

# ===== 상태 파일 정리 =====
if [[ "$TARGET" == "all" ]]; then
  log "상태 파일 정리 중..."
  STATE_DIR="/home/ubuntu/color-config"
  if [ -d "$STATE_DIR" ]; then
    rm -rf "$STATE_DIR"
    log "상태 디렉터리 제거: $STATE_DIR"
  fi
fi

log "=== 정리 완료: $TARGET ==="
