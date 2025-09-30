#!/bin/bash

echo "========================================"
echo "[DEPLOY] Blue/Green 무중단 배포 시작 (운영)"
echo "========================================"

# 배포 전 디스크 공간 확인 및 정리
echo "[CLEANUP] 배포 전 디스크 공간 확인..."
DISK_USAGE=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
echo "[INFO] 현재 디스크 사용률: ${DISK_USAGE}%"

if [ "$DISK_USAGE" -gt 80 ]; then
  echo "[WARN] 디스크 사용률 80% 초과! 자동 정리 시작..."
  
  # Docker 정리
  echo "[CLEANUP] 사용하지 않는 Docker 이미지/컨테이너 정리..."
  docker system prune -f --filter "until=24h"
  
  # 오래된 로그 삭제 (7일 이상)
  echo "[CLEANUP] 오래된 로그 파일 정리..."
  find ~/dataracy-prod/logs -name "*.log" -type f -mtime +7 -delete 2>/dev/null || true
  
  NEW_USAGE=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
  echo "[INFO] 정리 후 디스크 사용률: ${NEW_USAGE}%"
else
  echo "[INFO] 디스크 공간 충분"
fi

cd "$(dirname "$0")/../blue-green"

if [ ! -f current_color_prod ]; then
  echo "blue" > current_color_prod
fi

chmod +x ./switch-prod.sh
./switch-prod.sh

echo "========================================"
echo "[DEPLOY] 배포 완료! 현재 서비스 중인 인스턴스: $(cat current_color_prod)"
echo "========================================"
