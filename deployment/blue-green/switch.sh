#!/bin/bash

CURRENT=$(cat ./current_color)

if [ "$CURRENT" == "blue" ]; then
  NEXT="green"
  CURRENT_COMPOSE="../docker/docker-compose-green.yml"
else
  NEXT="blue"
  CURRENT_COMPOSE="../docker/docker-compose-blue.yml"
fi

echo "[INFO] 현재 배포 중인 컨테이너: $CURRENT"
echo "[INFO] 새로운 컨테이너로 전환합니다: $NEXT"

# ✅ 새 컨테이너 실행
docker compose -f "$CURRENT_COMPOSE" up -d --build

echo "[INFO] 새로운 컨테이너 Health Check 대기 중..."
for i in {1..20}; do
  STATUS=$(docker inspect --format='{{json .State.Health.Status}}' backend-${NEXT} 2>/dev/null || echo "null")
  if [ "$STATUS" == "\"healthy\"" ]; then
    echo "[SUCCESS] backend-${NEXT} 컨테이너가 정상적으로 실행되었습니다."
    break
  else
    echo "  [$i/15] 아직 준비되지 않음... (상태: $STATUS)"
    sleep 5
  fi
done

# 🔥 실패한 경우 배포 중단
if [ "$STATUS" != "\"healthy\"" ]; then
  echo "[ERROR] backend-${NEXT} 컨테이너가 정상 상태가 아닙니다. 배포를 중단합니다."
  exit 1
fi

# ✅ nginx 업스트림 설정은 항상 localhost:8080
echo "upstream backend { server localhost:8080; }" > ../nginx/upstream-blue-green.conf

if docker ps -a --format '{{.Names}}' | grep -q '^nginx-proxy$'; then
  docker restart nginx-proxy || {
    echo "[ERROR] nginx-proxy 재시작 실패"
    docker rm -f nginx-proxy || true
    docker compose -f "$CURRENT_COMPOSE" up -d nginx
  }
else
  docker compose -f "$CURRENT_COMPOSE" up -d nginx
fi

echo "[INFO] 이전 컨테이너 종료 중: backend-${CURRENT}"
docker rm -f backend-${CURRENT} || echo "[WARN] backend-${CURRENT} 제거 실패 또는 이미 없음"

echo "$NEXT" > ./current_color
echo "[DONE] 무중단 배포 완료! 현재 활성 인스턴스는 [$NEXT]"
