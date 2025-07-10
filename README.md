# 📊 Dataracy - 사용자 행동 로그 기반 관측 시스템

## Real-Time Behavioral Logging & Monitoring with Kafka, Elasticsearch, Redis, Prometheus, Grafana

---

## 🧩 프로젝트 개요

> **Dataracy**는 사용자 행동(클릭, 이동, 체류 시간 등)을 Kafka → Elasticsearch로 비동기 수집하고, Kibana 대시보드 및 Prometheus + Grafana를 통해 실시간 분석/시각화하는 고성능 로그 수집 시스템입니다.

- ✅ Spring Boot 3.2.5 + Java 17 기반
- ✅ Kafka 기반 로그 비동기 처리
- ✅ Elasticsearch 분석 최적화 (ILM, Mapping, Index Template)
- ✅ Prometheus + Grafana → 실시간 응답/트래픽 모니터링
- ✅ Redis로 익명 사용자 로그 병합 추적 지원
- ✅ AOP + MDC 기반 체계적인 로그 흐름 설계
- ✅ 헥사고날 아키텍처 + DDD + Clean Code 기반

---

## 🧱 시스템 아키텍처

```plaintext
User
 └── HTTP Request
      └── 🔍 [Filter] MDC 추적 ID, IP, Path 등 수집
           └── 🎯 [@TrackClick / @TrackNavigation] 사용자 행동 분류
                └── 🕓 DB/외부 API 응답시간 측정 (AOP)
                     └── 📦 Kafka Producer 전송
                          └── 🧵 Kafka Consumer → Elasticsearch 저장
                               └── 📊 Kibana 대시보드 시각화
      └── 📡 Prometheus /actuator/prometheus 수집
           └── 📈 Grafana 실시간 메트릭 분석
