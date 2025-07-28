#!/bin/bash

echo "========================================"
echo "[DEV DEPLOY] 개발 서버 무중단 배포 실행"
echo "========================================"

cd "$(dirname "$0")/../blue-green" || { echo "[ERROR] 작업 디렉터리 이동 실패"; exit 1; }

# switch-dev.sh 실행
chmod +x ./switch-dev.sh
./switch-dev.sh

echo "========================================"
echo "[DEPLOY] 배포 완료! 현재 서비스 중인 인스턴스: $(cat /home/ubuntu/color-config/current_color_dev)"
echo "========================================"
