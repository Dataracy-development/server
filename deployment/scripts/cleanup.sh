#!/bin/bash
# cleanup.sh — 정리 스크립트

set -Eeuo pipefail

log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*"; }
fail() { log "[ERROR] $*"; exit 1; }

ENVIRONMENT=${1:-"all"}

log "========================================"
log "[CLEANUP] Dataracy Blue/Green 배포 시스템 정리"
log "========================================"

if [ "$ENVIRONMENT" = "dev" ] || [ "$ENVIRONMENT" = "all" ]; then
  log "[INFO] 개발 환경 정리 중..."
  
  # 개발 환경 컨테이너 정리
  docker stop backend-blue backend-green nginx-proxy-dev 2>/dev/null || true
  docker rm -f backend-blue backend-green nginx-proxy-dev 2>/dev/null || true
  
  # 개발 환경 네트워크 정리
  docker network rm dataracy-network-dev 2>/dev/null || true
  
  log "[SUCCESS] 개발 환경 정리 완료"
fi

if [ "$ENVIRONMENT" = "prod" ] || [ "$ENVIRONMENT" = "all" ]; then
  log "[INFO] 운영 환경 정리 중..."
  
  # 운영 환경 컨테이너 정리
  docker stop backend-prod-blue backend-prod-green nginx-proxy-prod 2>/dev/null || true
  docker rm -f backend-prod-blue backend-prod-green nginx-proxy-prod 2>/dev/null || true
  
  # 운영 환경 네트워크 정리
  docker network rm dataracy-network-prod 2>/dev/null || true
  
  log "[SUCCESS] 운영 환경 정리 완료"
fi

if [ "$ENVIRONMENT" = "all" ]; then
  # 상태 파일 정리
  rm -f /home/ubuntu/color-config/current_color_dev
  rm -f /home/ubuntu/color-config/current_color_prod
  
  log "[SUCCESS] 상태 파일 정리 완료"
fi

log "[DONE] 정리 완료!"
log "========================================"
