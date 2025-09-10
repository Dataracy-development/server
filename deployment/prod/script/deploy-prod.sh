#!/bin/bash

echo "========================================"
echo "[DEPLOY] (PROD) Blue/Green 무중단 배포 시작"
echo "========================================"

# 현재 스크립트 기준으로 prod/blue-green 폴더로 이동
cd "$(dirname "$0")/../blue-green" || {
  echo "[ERROR] blue-green 디렉터리를 찾을 수 없습니다." >&2
  exit 1
}

# 최초 실행 시 기본 색상 파일 생성
if [ ! -f current_color_prod ]; then
  echo "blue" > current_color_prod
fi

# 전환 스크립트 실행
chmod +x ./switch-prod.sh
./switch-prod.sh

echo "========================================"
echo "[DEPLOY] (PROD) 배포 완료! 현재 서비스 중인 인스턴스: $(cat current_color_prod)"
echo "========================================"
