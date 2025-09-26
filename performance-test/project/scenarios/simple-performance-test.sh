#!/bin/bash

echo "=== 프로젝트 조회수 동기화 성능 테스트 ==="
echo ""

# 테스트 데이터 생성
echo "1. 테스트 데이터 생성 중..."
for i in {1..50}; do
  viewerId="perf_test_${i}_$(date +%s)_${RANDOM}"
  curl -s -H "X-Viewer-ID: $viewerId" "http://localhost:8080/api/v1/projects/$((i % 5 + 1))" > /dev/null
done
echo "   - 50개의 프로젝트 조회 요청 완료"
echo ""

# 워커 실행 대기
echo "2. 워커 실행 대기 중... (25초)"
sleep 25

# 현재 조회수 확인
echo "3. 현재 프로젝트 조회수 확인:"
for i in {1..5}; do
  viewCount=$(curl -s "http://localhost:8080/api/v1/projects/$i" | grep -o '"viewCount":[0-9]*' | cut -d':' -f2)
  echo "   - 프로젝트 $i: ${viewCount:-0}회"
done
echo ""

echo "=== 테스트 완료 ==="
echo "개별 처리 버전에서 워커가 실행되어 Redis 데이터가 DB로 동기화되었습니다."
echo "서버 로그에서 실제 쿼리 수와 실행 시간을 확인하세요."
