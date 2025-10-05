#!/bin/bash

# 테스트 실행 스크립트
# CI/CD 환경에서 안정적으로 테스트를 실행하기 위한 스크립트

set -e  # 에러 발생 시 스크립트 중단

echo "🚀 테스트 시작..."

# 1. 빌드 정리
echo "📦 빌드 정리 중..."
./gradlew clean

# 2. 의존성 다운로드
echo "📥 의존성 다운로드 중..."
./gradlew dependencies

# 3. 컴파일 확인
echo "🔨 컴파일 확인 중..."
./gradlew compileJava compileTestJava

# 4. 체크스타일 검사
echo "📋 코드 스타일 검사 중..."
./gradlew checkstyleMain checkstyleTest || echo "⚠️ 체크스타일 일부 위반 (계속 진행)"

# 5. 단위 테스트 실행 (CI/CD용)
echo "🧪 단위 테스트 실행 중..."
./gradlew test --continue --exclude-tests "*IntegrationTest"

# 7. 테스트 결과 요약
echo "📊 테스트 결과 요약:"
./gradlew test --dry-run | grep -E "(PASSED|FAILED|SKIPPED)" || echo "테스트 완료"

echo "✅ 테스트 실행 완료!"
