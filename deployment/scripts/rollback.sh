#!/bin/bash
set -euo pipefail

# ===== 공통 유틸 =====
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

# ===== 사용법 =====
if [ $# -ne 1 ]; then
  echo "사용법: $0 <dev|prod>"
  echo "예시: $0 dev"
  exit 1
fi

ENV="$1"
if [[ "$ENV" != "dev" && "$ENV" != "prod" ]]; then
  fail "환경은 dev 또는 prod여야 합니다."
fi

# ===== 경로 설정 =====
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
BLUE_GREEN_DIR="$SCRIPT_DIR/../${ENV}/blue-green"
STATE_DIR="/home/ubuntu/color-config"
CUR_FILE="$STATE_DIR/current_color_${ENV}"

# ===== 현재 색상 확인 =====
if [ ! -f "$CUR_FILE" ]; then
  fail "현재 색상 파일이 없습니다: $CUR_FILE"
fi

CUR="$(tr -d '[:space:]' < "$CUR_FILE")"
[[ "$CUR" =~ ^(blue|green)$ ]] || fail "current_color_${ENV} 값 오류: $CUR"

# 이전 색상으로 롤백
PREV=$([[ "$CUR" == "blue" ]] && echo green || echo blue)

log "=== ${ENV^^} 롤백 시작: $CUR → $PREV ==="

# ===== 롤백 스크립트 실행 =====
if [ -f "$BLUE_GREEN_DIR/rollback-${ENV}.sh" ]; then
  log "롤백 스크립트 실행: rollback-${ENV}.sh"
  chmod +x "$BLUE_GREEN_DIR/rollback-${ENV}.sh"
  "$BLUE_GREEN_DIR/rollback-${ENV}.sh"
else
  fail "롤백 스크립트를 찾을 수 없습니다: $BLUE_GREEN_DIR/rollback-${ENV}.sh"
fi

log "=== ${ENV^^} 롤백 완료 ==="
