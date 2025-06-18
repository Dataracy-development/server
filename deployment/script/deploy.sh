#!/bin/bash

echo "========================================"
echo "[DEPLOY] Blue/Green 무중단 배포 시작"
echo "========================================"

# shellcheck disable=SC2164
cd "$(dirname "$0")/../blue-green"

# 현재 상태 확인 또는 기본값 지정
if [ ! -f current_color ]; then
  echo "blue" > current_color
fi

./switch.sh

echo "========================================"
echo "[DEPLOY] 배포 완료! 현재 서비스 중인 인스턴스: $(cat current_color)"
echo "========================================"
