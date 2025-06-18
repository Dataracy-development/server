#!/bin/bash

# 현재 배포 중인 색상 확인
CURRENT=$(cat ./current_color)

# 다음 배포 대상 결정
if [ "$CURRENT" == "blue" ]; then
  NEXT="green"
  CURRENT_COMPOSE="../docker/docker-compose-green.yml"
else
  NEXT="blue"
  CURRENT_COMPOSE="../docker/docker-compose-blue.yml"
fi

echo "[INFO] 현재 배포 중인 컨테이너: $CURRENT"
echo "[INFO] 새로운 컨테이너로 전환합니다: $NEXT"

# 새 컨테이너 실행 (docker-compose up -d)
docker compose -f $CURRENT_COMPOSE up -d --build

# 새 컨테이너 health check 대기
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

# nginx 라우팅 설정 변경
echo "[INFO] nginx 업스트림 설정 변경 중..."
if [ "$NEXT" == "blue" ]; then
  echo "upstream backend { server backend-blue:8080; }" > ../nginx/upstream-blue-green.conf
else
  echo "upstream backend { server backend-green:8080; }" > ../nginx/upstream-blue-green.conf
fi

# nginx 재시작
echo "[INFO] nginx 컨테이너 재시작"
docker restart nginx-proxy

# 상태 갱신
echo "$NEXT" > ./current_color
echo "[DONE] 무중단 배포 완료! 현재 활성 인스턴스는 [$NEXT]"
