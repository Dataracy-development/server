#!/bin/bash

echo "========================================"
echo "[DEPLOY] Blue/Green 무중단 배포 시작"
echo "========================================"

cd "$(dirname "$0")/../blue-green"

# current_color 파일 없으면 기본 blue로 생성
if [ ! -f current_color ]; then
  echo "blue" > current_color
fi

chmod +x ./switch.sh
./switch.sh

echo "========================================"
echo "[DEPLOY] 배포 완료! 현재 서비스 중인 인스턴스: $(cat current_color)"
echo "========================================"
