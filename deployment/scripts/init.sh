#!/bin/bash
# init.sh — 초기 설정 스크립트

set -Eeuo pipefail

log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*"; }
fail() { log "[ERROR] $*"; exit 1; }

log "========================================"
log "[INIT] Dataracy Blue/Green 배포 시스템 초기화"
log "========================================"

# Docker 네트워크 생성
log "[INFO] Docker 네트워크 생성 중..."

# 개발 환경 네트워크
if ! docker network ls | grep -q "dataracy-network-dev"; then
  docker network create dataracy-network-dev
  log "[SUCCESS] dataracy-network-dev 네트워크 생성 완료"
else
  log "[INFO] dataracy-network-dev 네트워크가 이미 존재합니다"
fi

# 운영 환경 네트워크
if ! docker network ls | grep -q "dataracy-network-prod"; then
  docker network create dataracy-network-prod
  log "[SUCCESS] dataracy-network-prod 네트워크 생성 완료"
else
  log "[INFO] dataracy-network-prod 네트워크가 이미 존재합니다"
fi

# 상태 디렉터리 생성
log "[INFO] 상태 디렉터리 생성 중..."
mkdir -p /home/ubuntu/color-config
mkdir -p /home/ubuntu/env-config
mkdir -p /home/ubuntu/ssl/cloudflare

# 초기 색상 설정
if [ ! -f "/home/ubuntu/color-config/current_color_dev" ]; then
  echo "blue" > /home/ubuntu/color-config/current_color_dev
  log "[SUCCESS] 개발 환경 초기 색상 설정: blue"
fi

if [ ! -f "/home/ubuntu/color-config/current_color_prod" ]; then
  echo "blue" > /home/ubuntu/color-config/current_color_prod
  log "[SUCCESS] 운영 환경 초기 색상 설정: blue"
fi

# 스크립트 실행 권한 부여
log "[INFO] 스크립트 실행 권한 설정 중..."
chmod +x deployment/dev/script/deploy.sh
chmod +x deployment/dev/blue-green/switch-dev.sh
chmod +x deployment/prod/script/deploy-prod.sh
chmod +x deployment/prod/blue-green/switch-prod.sh

log "[DONE] 초기화 완료!"
log "========================================"
