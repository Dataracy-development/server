#!/bin/bash
# status.sh — 상태 확인 스크립트

set -Eeuo pipefail

log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*"; }

log "========================================"
log "[STATUS] Dataracy Blue/Green 배포 시스템 상태"
log "========================================"

# Docker 네트워크 상태
log "[INFO] Docker 네트워크 상태:"
docker network ls | grep dataracy || log "[WARN] Dataracy 네트워크가 없습니다"

# 개발 환경 상태
log ""
log "[INFO] 개발 환경 상태:"
if [ -f "/home/ubuntu/color-config/current_color_dev" ]; then
  CURRENT_DEV=$(cat /home/ubuntu/color-config/current_color_dev)
  log "  현재 활성 색상: $CURRENT_DEV"
else
  log "  [WARN] 개발 환경 색상 파일이 없습니다"
fi

# 개발 환경 컨테이너
log "  개발 환경 컨테이너:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(backend-dev-blue|backend-dev-green|nginx-proxy-dev)" || log "  [WARN] 개발 환경 컨테이너가 실행 중이 아닙니다"

# 운영 환경 상태
log ""
log "[INFO] 운영 환경 상태:"
if [ -f "/home/ubuntu/color-config/current_color_prod" ]; then
  CURRENT_PROD=$(cat /home/ubuntu/color-config/current_color_prod)
  log "  현재 활성 색상: $CURRENT_PROD"
else
  log "  [WARN] 운영 환경 색상 파일이 없습니다"
fi

# 운영 환경 컨테이너
log "  운영 환경 컨테이너:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(backend-prod-blue|backend-prod-green|nginx-proxy-prod)" || log "  [WARN] 운영 환경 컨테이너가 실행 중이 아닙니다"

# 포트 사용 현황
log ""
log "[INFO] 포트 사용 현황:"
netstat -tlnp | grep -E ":(80|81|443|444|8081|8082|8083|8084)" || log "  [WARN] 관련 포트가 사용 중이 아닙니다"

log "========================================"
