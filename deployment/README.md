# Dataracy Blue/Green 무중단 배포 시스템

## 개요

이 시스템은 하나의 EC2 인스턴스에서 개발(dev)과 운영(prod) 환경을 동시에 운영하며, 각각 블루-그린 무중단 배포를 지원합니다.

## 아키텍처

### 도메인 구조

- **프론트엔드**: `https://dataracy.co.kr`
- **개발 서버**: `https://dev.api.dataracy.co.kr` (HTTP/HTTPS 모두 지원)
- **운영 서버**: `https://api.dataracy.co.kr` (HTTPS 강제, HTTP→HTTPS 리다이렉트)

### 네트워크 구조

- `dataracy-network-dev`: 개발 환경 컨테이너들
- `dataracy-network-prod`: 운영 환경 컨테이너들
- 각 환경별로 독립적인 NGINX 프록시 사용

### 컨테이너 구조

- **개발**: `backend-blue`, `backend-green`, `nginx-proxy-dev`
- **운영**: `backend-prod-blue`, `backend-prod-green`, `nginx-proxy-prod`

## 디렉터리 구조

```
deployment/
├── nginx/                          # NGINX 설정 (각 환경별로 분리)
├── dev/                           # 개발 환경
│   ├── blue-green/
│   │   └── switch-dev.sh          # 개발 블루-그린 스위치
│   ├── docker/
│   │   ├── docker-compose-blue-dev.yml
│   │   └── docker-compose-green-dev.yml
│   ├── nginx/
│   │   ├── nginx-dev.conf
│   │   └── upstream-blue-green-dev.conf
│   └── script/
│       └── deploy.sh
├── prod/                          # 운영 환경
│   ├── blue-green/
│   │   └── switch-prod.sh         # 운영 블루-그린 스위치
│   ├── docker/
│   │   ├── docker-compose-blue-prod.yml
│   │   └── docker-compose-green-prod.yml
│   ├── nginx/
│   │   ├── nginx-prod.conf
│   │   └── upstream-blue-green-prod.conf
│   └── script/
│       └── deploy-prod.sh
└── scripts/                       # 관리 스크립트
    ├── init.sh                    # 초기 설정
    ├── status.sh                  # 상태 확인
    └── cleanup.sh                 # 정리
```

## 사용법

### 1. 초기 설정

```bash
# 개발 환경 초기화
cd ~/dataracy-dev/deployment/scripts
chmod +x *.sh
./init.sh

# 운영 환경 초기화
cd ~/dataracy-prod/deployment/scripts
chmod +x *.sh
./init.sh
```

### 2. 상태 확인

```bash
./status.sh
```

### 3. 수동 배포

```bash
# 개발 환경 배포
cd ~/dataracy-dev/deployment/dev/script
./deploy.sh

# 운영 환경 배포
cd ~/dataracy-prod/deployment/prod/script
./deploy-prod.sh
```

### 4. 롤백

```bash
# 직접 스위치 스크립트 실행 (반대 색상으로 전환)
cd ~/dataracy-dev/deployment/dev/blue-green
./switch-dev.sh

cd ~/dataracy-prod/deployment/prod/blue-green
./switch-prod.sh
```

### 5. 정리

```bash
# 특정 환경 정리
./cleanup.sh dev
./cleanup.sh prod

# 전체 정리
./cleanup.sh all
```

## 자동 배포

### GitHub Actions

- **develop 브랜치 푸시** → 개발 서버 블루-그린 배포 (`dev.api.dataracy.co.kr`)
- **main 브랜치 푸시** → 운영 서버 블루-그린 배포 (`api.dataracy.co.kr`)

### 배포 과정

1. 현재 활성 색상 확인
2. 반대 색상으로 새 이미지 빌드 및 푸시
3. 새 컨테이너 실행 및 헬스체크
4. Nginx upstream 설정 업데이트
5. Nginx 리로드
6. 이전 컨테이너 정리

## 주의사항

1. **네트워크 분리**: dev와 prod는 각각 별도의 Docker 네트워크를 사용
2. **Nginx 분리**: 각 환경별로 독립적인 Nginx 컨테이너 사용
3. **상태 관리**: `/home/ubuntu/color-config/` 디렉터리에 현재 색상 저장
4. **SSL 인증서**: Cloudflare Origin Certificate 사용
5. **헬스체크**: 각 컨테이너는 `/actuator/health` 엔드포인트로 헬스체크
6. **포트 분리**:
   - 개발: 80, 443, 8081, 8082
   - 운영: 80, 443, 8083, 8084

## 문제 해결

### 일반적인 문제

1. **컨테이너가 시작되지 않음**: Docker 이미지가 존재하는지 확인
2. **Nginx 설정 오류**: `docker exec nginx-proxy nginx -t`로 설정 검증
3. **네트워크 연결 문제**: Docker 네트워크가 생성되었는지 확인
4. **권한 문제**: 스크립트에 실행 권한이 있는지 확인

### 로그 확인

```bash
# 컨테이너 로그
docker logs backend-blue
docker logs nginx-proxy

# Nginx 로그
docker exec nginx-proxy tail -f /var/log/nginx/error.log
```

## 보안 고려사항

1. **HTTPS 강제**: 운영 환경은 HTTPS만 허용
2. **HSTS**: 운영 환경에 HSTS 헤더 적용
3. **CORS**: 적절한 CORS 설정으로 보안 강화
4. **네트워크 격리**: dev/prod 네트워크 분리로 보안 강화
