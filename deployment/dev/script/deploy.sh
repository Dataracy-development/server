#!/bin/bash

echo "========================================"
echo "[DEV DEPLOY] 개발 서버 무중단 배포 실행"
echo "========================================"

cd "$(dirname "$0")/../blue-green"

# 상태 파일이 없으면 초기화
if [ ! -f current_color_dev ]; then
  echo "blue" > current_color_dev
fi

# switch-dev.sh 실행
chmod +x ./switch-dev.sh
./switch-dev.sh

# 결과 출력
echo "========================================"
echo "[DEPLOY] 배포 완료! 현재 서비스 중인 인스턴스: $(cat current_color_dev)"
echo "========================================"
