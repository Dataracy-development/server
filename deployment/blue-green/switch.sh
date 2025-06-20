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

docker compose -f "$CURRENT_COMPOSE" up -d --build

echo "[INFO] 새로운 컨테이너 Health Check 대기 중..."
for i in {1..15}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")
  if [ "$STATUS" == "200" ]; then
    echo "[SUCCESS] $NEXT 컨테이너가 정상적으로 실행되었습니다."
    break
  else
    echo "  [$i/15] 아직 준비되지 않음... (상태: $STATUS)"
    sleep 3
  fi
done

echo "[INFO] nginx 업스트림 설정 변경 중..."
if [ "$NEXT" == "blue" ]; then
  echo "upstream backend { server backend-blue:8080; }" > /home/ubuntu/dataracy/nginx/upstream-blue-green.conf
else
  echo "upstream backend { server backend-green:8080; }" > /home/ubuntu/dataracy/nginx/upstream-blue-green.conf
fi

PID_80=$(sudo lsof -t -i :80)
if [ -n "$PID_80" ]; then
  echo "[WARN] 포트 80 사용 중 → PID $PID_80 종료 시도"
  sudo kill -9 $PID_80
  sleep 2
fi

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
