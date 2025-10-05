#!/bin/bash

# 로컬 전용 테스트 실행 스크립트
# 통합 테스트 포함하여 모든 테스트를 실행합니다.

set -e  # 에러 발생 시 스크립트 중단

echo "🚀 로컬 테스트 시작 (통합 테스트 포함)..."

# 1. 빌드 정리
echo "📦 빌드 정리 중..."
./gradlew clean

# 2. 의존성 다운로드
echo "📥 의존성 다운로드 중..."
./gradlew dependencies

# 3. 컴파일 확인
echo "🔨 컴파일 확인 중..."
./gradlew compileJava compileTestJava

# 4. 코드 품질 검사
echo "📋 코드 품질 검사 중..."
./gradlew checkstyleMain checkstyleTest spotlessCheck

# 5. 단위 테스트 실행
echo "🧪 단위 테스트 실행 중..."
./gradlew test --continue --exclude-tests "*IntegrationTest"

# 6. 통합 테스트 실행 (로컬에서만)
echo "🔗 통합 테스트 실행 중..."
./gradlew test --continue --tests "*IntegrationTest" || echo "⚠️ 통합 테스트 일부 실패 (외부 서비스 의존성)"

# 7. 전체 테스트 결과
echo "📊 전체 테스트 결과:"
./gradlew test --continue

echo "✅ 로컬 테스트 완료!"
