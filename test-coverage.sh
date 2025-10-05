#!/bin/bash

# 실무적 테스트 커버리지 실행 스크립트
# 개발자가 PR 전에 커버리지를 확인할 때 사용

set -e  # 에러 발생 시 스크립트 중단

echo "🚀 테스트 커버리지 확인 시작..."

# 1. 빌드 정리
echo "📦 빌드 정리 중..."
./gradlew clean

# 2. 테스트 실행
echo "🧪 테스트 실행 중..."
./gradlew test --continue

# 3. 커버리지 리포트 생성
echo "📊 커버리지 리포트 생성 중..."
./gradlew jacocoTestReport

# 4. 커버리지 검증
echo "✅ 커버리지 검증 중..."
./gradlew jacocoTestCoverageVerification

# 5. 결과 요약
echo "📋 커버리지 결과 요약:"
if [ -f "build/reports/jacoco/test/jacocoTestReport.xml" ]; then
    echo "✅ HTML 리포트: build/reports/jacoco/test/html/index.html"
    echo "✅ XML 리포트: build/reports/jacoco/test/jacocoTestReport.xml"
    echo "✅ 커버리지 검증 완료"
else
    echo "❌ 커버리지 리포트 생성 실패"
    exit 1
fi

echo "✅ 테스트 커버리지 확인 완료!"
echo "💡 HTML 리포트를 브라우저에서 열어서 상세 커버리지를 확인하세요."
