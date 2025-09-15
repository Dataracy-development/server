#!/bin/bash

echo "========================================"
echo "[DEPLOY] Blue/Green 무중단 배포 시작 (운영)"
echo "========================================"

cd "$(dirname "$0")/../blue-green"

if [ ! -f current_color_prod ]; then
  echo "blue" > current_color_prod
fi

chmod +x ./switch-prod.sh
./switch-prod.sh

echo "========================================"
echo "[DEPLOY] 배포 완료! 현재 서비스 중인 인스턴스: $(cat current_color_prod)"
echo "========================================"
