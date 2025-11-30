> 1인 프로젝트의 가장 큰 고민 "코드 리뷰를 누가 해주지?" - LLM 기반 자동 코드 리뷰 시스템 구축 경험을 기록합니다.

Dataracy는 데이터 분석가들이 서로의 프로젝트와 데이터셋을 공유하는 플랫폼입니다. 혼자 개발하다 보니 가장 아쉬운 점이 생겼습니다.

**"내 코드를 누가 리뷰해주지?"**

팀 프로젝트였다면 동료에게 받았을 피드백, 놓친 버그, 더 나은 설계 아이디어...

혼자 개발하면서 가장 아쉬운 부분이었습니다.

---

# 📝 1인 프로젝트의 딜레마

## 문제 상황

**배경:**
- 백엔드 혼자 개발 중 (Java + Spring Boot)
- 기능 구현은 되는데... 이게 맞게 짠 건가?
- 놓친 버그는 없을까?
- 더 나은 설계 방법은 없을까?

**실제로 겪은 문제들:**

```java
// 예시 1: 트랜잭션 범위 실수
@Service
public class UserService {
    
    @Transactional  // ❌ 여기 있어야 하나? UseCase에?
    public void createUser(CreateUserRequest dto) {
        // ... 복잡한 로직
    }
}

// 나중에 발견: UseCase에 있어야 했음!
```

```java
// 예시 2: 동시성 문제
public void updateViewCount(Long projectId) {
    Project project = projectRepository.findById(projectId);
    project.incrementViewCount();  // ❌ 동시 요청 시 문제!
    projectRepository.save(project);
}

// 나중에 발견: Optimistic Lock 필요했음!
```

**😱 문제:**
- 혼자 개발하니 이런 문제를 **배포 전에 발견하기 어려움**
- 코드 리뷰 없이 머지 → 운영 환경에서 문제 발생
- "코드 리뷰를 받을 수 있다면..."

---

# 💡 해결 방안 탐색

## 고민한 방법들

### 방안 1: 온라인 커뮤니티에 요청

- 장점: 실제 개발자의 리얼 피드백
- 단점: 매번 요청하기 부담, 응답 느림, 코드 공개 꺼려짐
- 결론: 지속 가능하지 않음 ✗

### 방안 2: 코드 리뷰 툴 (SonarQube, CodeClimate)

- 장점: 자동화, 빠른 피드백
- 단점: 형식적 규칙만 체크 (네이밍, 포맷팅), 설계 리뷰 불가능
- 결론: 스타일 체크만 됨 ✗

### 방안 3: LLM 기반 커스텀 리뷰어 (선택 ✅)

- 장점: 설계, 구조, 정책 준수까지 리뷰 가능, 즉각 피드백, 커스텀 가능
- 단점: GPT API 비용 (~$0.01/PR), 구현 필요
- 결론: 가장 실무적인 피드백 가능 ✅

**선택 이유:**

- GPT-4o는 Java/Spring 패턴 이해 가능
- PR마다 자동 리뷰 → 즉각 피드백
- 프로젝트 정책을 프롬프트에 반영 가능
- GitHub와 통합 가능

---

# 🔨 구현: LLM 코드 리뷰 시스템

## 시스템 설계

**전체 흐름:**

```
GitHub PR 생성
    ↓
GitHub Webhook 발송
    ↓
Flask 서버 수신
    ↓
PR Diff 파싱 (파일별 분리)
    ↓
GPT-4o 리뷰 요청
├─ 1. 전체 요약 (Summary)
└─ 2. 파일별 상세 리뷰
    ↓
GitHub Comment 등록
```

**핵심 설계 결정:**

1. **Webhook 방식 선택**
   - GitHub Actions보다 유연함
   - 실시간 피드백 가능
   - 다른 이벤트도 처리 가능

2. **파일별 리뷰 분리**
   - 전체 diff를 한 번에 → 토큰 초과 위험
   - 파일별로 나눠서 → 상세한 리뷰 가능

3. **2단계 리뷰**
   - Summary: PR 전체 변경사항 요약
   - File Review: 파일별 상세 피드백

<br/>

## 구현 코드

### 1. GitHub Webhook 수신 (`webhook.py`)

```python
from flask import Flask, request, abort
import hmac
import hashlib

app = Flask(__name__)

def verify_signature(payload: bytes, signature: str) -> bool:
    """GitHub Webhook Signature 검증"""
    mac = hmac.new(GITHUB_SECRET.encode(), msg=payload, digestmod=hashlib.sha256)
    expected = 'sha256=' + mac.hexdigest()
    return hmac.compare_digest(expected, signature)

@app.route("/webhook", methods=["POST"])
def webhook():
    # 🔐 보안 검증 (HMAC)
    payload = request.get_data()
    signature = request.headers.get("X-Hub-Signature-256")
    if not verify_signature(payload, signature):
        abort(400, "Invalid signature")

    # 📦 PR 이벤트 여부 확인
    event = request.headers.get("X-GitHub-Event")
    if event != "pull_request":
        return "Ignored", 200

    data = request.json
    action = data.get("action")
    pr_number = data["pull_request"]["number"]
    diff_url = data["pull_request"]["diff_url"]

    if action != "opened":
        return "Ignored", 200

    # 📑 Diff 다운로드
    diff_text = requests.get(diff_url).text

    # ✨ 1. 전체 요약 코멘트
    summary_prompt = build_summary_prompt(diff_text)
    summary_response = call_gpt(summary_prompt)
    post_github_comment(pr_number, f"🚀 **GPT PR 전체 요약**\n\n{summary_response}")

    # ✨ 2. 파일별 리뷰 코멘트들
    review_comments = generate_review_comments(diff_text)
    for comment in review_comments:
        post_github_comment(pr_number, comment["body"])

    return "Review posted", 200
```

**핵심 포인트:**

- HMAC 서명 검증으로 보안
- PR opened 이벤트만 처리
- Diff URL에서 변경사항 다운로드
- 요약 + 파일별 리뷰 2단계 처리

<br/>

### 2. Diff 파싱 (`diff_parser.py`)

```python
import re

def extract_changed_files(diff_text: str) -> list[dict]:
    """
    전체 PR diff에서 변경된 파일들의 경로와 해당 diff 내용 추출
    """
    pattern = re.compile(r"^diff --git a/(.+?) b/.*?$", re.MULTILINE)
    matches = pattern.finditer(diff_text)

    files = []
    lines = diff_text.splitlines()
    indices = [match.start() for match in matches] + [len(diff_text)]

    for i in range(len(indices) - 1):
        start = indices[i]
        end = indices[i + 1]
        segment = diff_text[start:end]
        path_match = re.search(r"^diff --git a/(.+?) b/", segment)
        if path_match:
            files.append({
                "path": path_match.group(1).strip(),
                "content": segment.strip()
            })

    return files
```

**왜 이렇게?**

- GitHub diff는 `diff --git a/file b/file` 형식
- 정규식으로 파일 경로 추출
- 각 파일의 diff 내용 분리
- GPT에게 파일별로 전달 가능

<br/>

### 3. GPT 리뷰 생성 (`reviewer.py`)

```python
from prompt_file_review import build_file_review_prompt
from diff_parser import extract_changed_files
from utils import call_gpt
import re

def generate_review_comments(diff_text: str) -> list[dict]:
    """파일 기반 리뷰 코멘트를 생성하여 리스트로 반환"""
    comments = []
    parsed_files = extract_changed_files(diff_text)

    for file in parsed_files:
        path = file["path"]
        file_diff = file["content"]

        # GPT에게 리뷰 요청
        prompt = build_file_review_prompt(file_diff)
        gpt_response = call_gpt(prompt).strip()

        # 🚀 기준으로 여러 코멘트 블럭 분리
        review_blocks = re.split(r"\n?🚀", gpt_response)
        for block in review_blocks:
            block = block.strip()
            if not block:
                continue
            comments.append({
                "body": f"[🚀 `{path}` 파일 리뷰]\n\n💬 {block}"
            })

    return comments
```

**핵심 로직:**

- 파일별로 diff 추출
- 각 파일마다 GPT-4o에게 리뷰 요청
- 응답을 🚀 기준으로 분리 (여러 이슈 나눔)
- GitHub Comment 형식으로 변환

<br/>

### 4. GPT 프롬프트 설계 (`prompt_file_review.py`)

```python
def build_file_review_prompt(file_diff: str) -> str:
    return f"""
당신은 Java + Spring 기반 SaaS 백엔드 실무 코드 리뷰 전문가입니다.

아래는 PR에서 변경된 단일 파일의 diff입니다.  
**형식적인 스타일 지적은 생략하고**, 실무적으로 중요한 코드 품질, 구조, 확장성 문제만 리뷰하세요.

---

🎯 리뷰 목적:
- 시스템 품질 향상에 실질적인 도움이 되는 리뷰만 제공합니다.
- 사소한 네이밍, 스타일, 포맷팅 지적은 생략합니다.
- **도메인 모델링, 설계, 구조, 책임 분리, 예외 처리, AOP 분리** 중심으로 검토합니다.

---

✅ 대표적인 리뷰 대상 예시:
- 🔒 동시성 문제 가능성 / 락 누락
- ❗️예외 처리 누락, CustomException 미사용
- 📦 로깅, 인증, 검증 등 AOP 분리 필요 여부
- 🔧 단일 책임 원칙(SRP) 위반
- 🧱 도메인 → 인프라 직접 의존 (Clean Architecture 위배)
- 🔁 트랜잭션 범위가 잘못 지정되었거나 위치가 부적절함

---

🔽 아래는 리뷰 대상 파일의 diff입니다:

{file_diff}
"""
```

**프롬프트 설계 포인트:**

- "형식적 스타일 지적 생략" → 의미 있는 리뷰만
- 프로젝트 정책 반영 (AOP, CustomException, Clean Architecture)
- 실무 중심 (동시성, 트랜잭션, 책임 분리)
- 한국어로 친절한 설명 요청

<br/>

### 5. GitHub API 연동

```python
import requests

def post_github_comment(pr_number: int, body: str):
    """GitHub PR에 코멘트 등록"""
    requests.post(
        f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments",
        headers={
            "Authorization": f"Bearer {GITHUB_TOKEN}",
            "Accept": "application/vnd.github+json",
        },
        json={"body": body},
    )
```

**간단하지만 핵심:**

- GitHub Issues API 사용 (PR도 Issue의 일종)
- Token 인증으로 안전하게
- Markdown 형식 지원

<br/>

### 6. 재시도 메커니즘 (`retry_worker.py`)

```python
import json
import time

def process_retry_queue():
    """실패한 GPT 호출 재시도"""
    with open("retry_queue.json", "r") as f:
        lines = f.readlines()

    # 파일 초기화
    open("retry_queue.json", "w").close()

    for line in lines:
        entry = json.loads(line)
        prompt = entry.get("prompt", "")
        
        response = call_gpt(prompt)
        
        if response.startswith("[GPT 호출 실패]"):
            save_failed_prompt(prompt, response)  # 다시 큐에
        else:
            print("✅ 성공")
        
        time.sleep(2)  # Rate Limit 방지
```

**왜 필요?**

- GPT API 일시적 실패 가능 (Rate Limit, Timeout)
- 실패한 리뷰를 나중에 재시도
- 모든 파일이 리뷰받도록 보장

<br/>

## Docker 배포

`docker-compose.yml`:

```yaml
version: '3.8'

services:
  codereview-llm:
    container_name: codereview-llm
    build:
      context: .
    ports:
      - "8000:8000"
    env_file:
      - .env
    restart: always
    command: python webhook.py
```

`Dockerfile`:

```dockerfile
FROM python:3.11-slim

WORKDIR /app
COPY . .
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 8000
CMD ["python", "webhook.py"]
```

**배포:**

```bash
$ docker-compose up -d
```

→ EC2에서 24/7 실행, PR마다 자동 리뷰!

---

# 🎯 실제 사용 결과

## GitHub 설정

**1. Webhook 등록:**

```
GitHub Repo → Settings → Webhooks → Add webhook
- Payload URL: http://my-server.com:8000/webhook
- Content type: application/json
- Secret: [GITHUB_SECRET]
- Events: Pull requests
```

**2. 환경 변수 설정 (`.env`):**

```bash
GITHUB_TOKEN=ghp_xxxxxxxxxxxxx
GITHUB_REPO=username/dataracy
GITHUB_SECRET=my-webhook-secret
OPENAI_API_KEY=sk-xxxxxxxxxxxxx
```

<br/>

## 실제 리뷰 예시

**PR #47: 프로젝트 조회수 기능 추가**

코드를 작성하고 PR을 올리자마자 30초 후:

### GPT 리뷰 1: 전체 요약

```markdown
🚀 **GPT PR 전체 요약**

### 1. 변경 파일 요약

| 파일 | 변경 요약 |
|------|-----------|
| `ProjectService.java` | 조회수 증가 기능 추가 |
| `ProjectController.java` | 조회수 API 엔드포인트 추가 |

### 2. 주요 변경 목적
- 프로젝트 상세 조회 시 조회수 자동 증가

### 3. 공통 설계 정책 준수 여부
⚠️ **동시성 문제 가능성**
- `incrementViewCount()`가 동시 요청 시 조회수 누락 가능
- Optimistic Lock 또는 Redis 카운터 권장

### 4. 리팩토링 제안
- 조회수 증가를 별도 UseCase로 분리 권장
- 트랜잭션 범위 재검토 필요
```

### GPT 리뷰 2: 파일별 상세

```markdown
[🚀 `ProjectService.java` 파일 리뷰]

💬 **동시성 문제**

현재 코드:
\`\`\`java
public void incrementViewCount(Long projectId) {
    Project project = projectRepository.findById(projectId);
    project.incrementViewCount();
    projectRepository.save(project);
}
\`\`\`

**문제:**
- 동시에 2명이 조회하면 조회수가 1만 증가할 수 있음
- JPA Optimistic Lock 또는 Redis 원자적 연산 권장

**개선 제안:**
\`\`\`java
@Entity
public class Project {
    @Version
    private Long version;  // Optimistic Lock
    
    public void incrementViewCount() {
        this.viewCount++;
    }
}
\`\`\`
```

**😱 충격:**
- 내가 놓친 동시성 문제를 정확히 지적!
- 개선 방법까지 코드로 제시!
- "아, 이런 부분을 고려 못 했구나..." 깨달음

---

# 📊 사용 결과

## 정량적 효과

**3개월 사용 결과:**

| 항목 | Before<br/>(리뷰 없음) | After<br/>(LLM 리뷰) | 효과 |
|------|------------------------|----------------------|------|
| 코드 리뷰 | 0개 | 모든 PR | 100% 리뷰 ✅ |
| 발견된 이슈 | 배포 후 발견 | PR 단계에서 발견 | 조기 발견 |
| 동시성 버그 | 3건 (배포 후) | 0건 | 사전 차단 ✅ |
| 트랜잭션 실수 | 2건 | 0건 | 사전 차단 ✅ |
| 평균 리뷰 시간 | N/A | 30초 | 즉각 피드백 |

<br/>

## 실제로 발견한 문제들

### 발견 1: Distributed Lock 누락

**GPT 피드백:**
```
🚀 동시성 문제 발견

UserService.modifyUserInfo()에서 여러 사용자가 같은 닉네임으로 변경 시 중복 가능성.
Distributed Lock (@DistributedLock) 적용 권장.
```

**결과:**
- 즉시 Distributed Lock 적용
- 동시성 이슈 사전 방지
- [이전 트러블슈팅 참고](링크)

### 발견 2: N+1 문제

**GPT 피드백:**
```
🚀 N+1 쿼리 문제

Project 조회 시 Label을 for문으로 가져오고 있습니다.
fetchJoin 또는 @EntityGraph 사용 권장.
```

**결과:**
- 즉시 fetchJoin 적용
- 쿼리 50개 → 1개로 감소
- [성능 개선 트러블슈팅 참고](링크)

### 발견 3: 트랜잭션 위치 오류

**GPT 피드백:**
```
🚀 트랜잭션 경계 문제

@Transactional이 UseCase가 아닌 Service에 있습니다.
Clean Architecture 원칙상 UseCase에 위치해야 합니다.
```

**결과:**
- 트랜잭션 위치 이동
- 아키텍처 일관성 확보

<br/>

## 비용

**3개월 사용 비용:**

```
총 PR: 42개
평균 GPT 비용: $0.008/PR (파일 3-5개 기준)
총 비용: $0.336 (약 500원)

→ 월 167원으로 모든 PR 리뷰!
```

**놀라운 점:**
- 생각보다 훨씬 저렴함
- GPT-4o가 효율적 (토큰 적게 사용)
- 커피 한 잔 값으로 3개월 리뷰

---

# 🤔 한계와 개선

## 현재 한계

### 1. 전체 맥락 부족

GPT는 **변경된 파일만** 보기 때문에:

```
문제:
- 다른 파일과의 연관성 파악 불가
- 전체 아키텍처 맥락 부족

예시:
- Service에서 Port 호출하는데, Port 구현체가 없는지 확인 못 함
- DTO가 여러 곳에서 재사용되는지 파악 못 함
```

### 2. 비즈니스 로직 검증 불가

```
GPT는 "문법적으로 맞는가"만 판단:
- "재고 차감 로직이 맞는가?" → 판단 못 함
- "결제 금액 계산이 정확한가?" → 판단 못 함

→ 도메인 로직은 직접 검증 필요
```

### 3. 거짓 긍정 (False Positive)

```
가끔 잘못된 지적:
- "이 메서드는 너무 길어요" → 실제론 적절한 길이
- "예외 처리 누락" → 실제론 상위에서 처리함

→ GPT 피드백을 맹신하지 말고 판단 필요
```

<br/>

## 개선 방향

### 1. 프로젝트 컨텍스트 제공

```python
# 개선 아이디어:
def build_file_review_prompt_with_context(file_diff, related_files):
    return f"""
    [현재 파일 diff]
    {file_diff}
    
    [관련된 다른 파일들]
    {related_files}  # Port 인터페이스, DTO 등
    
    전체 맥락을 고려하여 리뷰하세요.
    """
```

→ 더 정확한 리뷰 가능

### 2. 프로젝트별 체크리스트

```python
프로젝트 정책:
- @Transactional은 UseCase에만
- 모든 예외는 CustomException 상속
- Port-Adapter 패턴 준수
- 모든 외부 API는 Retry 정책 필수

→ GPT가 이 체크리스트 기준으로 리뷰
```

### 3. 리뷰 품질 향상

- GPT-4o → GPT-4.5 (더 정확한 판단)
- Few-shot 예시 제공 (좋은 리뷰 샘플)
- 프로젝트 README 포함 (아키텍처 이해)

---

# 🚀 마치며

## 이번에 배운 것

**1인 프로젝트의 가장 큰 고민이 "피드백 부재"**였습니다.

하지만 LLM 코드 리뷰 시스템을 만들면서:

- ✅ 모든 PR마다 즉각 피드백
- ✅ 동시성, 트랜잭션, 설계 이슈 사전 발견
- ✅ 배포 전 품질 향상
- ✅ 월 167원으로 시니어 리뷰어 확보

**가장 인상 깊었던 점:**

처음에는 "GPT가 제대로 리뷰할 수 있을까?" 의심했는데, 실제로:
- 내가 놓친 동시성 문제 3건 발견
- 트랜잭션 위치 오류 2건 발견
- N+1 문제 조기 발견

**"완벽하진 않지만, 없는 것보다 훨씬 낫다"**

<br/>

## 다음에는

**1. 컨텍스트 확장**

현재는 변경된 파일만 보지만:
- 관련 파일들도 함께 제공
- 프로젝트 아키텍처 문서 포함
- 더 정확한 리뷰 기대

**2. 리뷰 품질 개선**

- Few-shot 예시로 리뷰 품질 향상
- 프로젝트 정책 체크리스트 강화
- GPT-4.5로 업그레이드

**3. 자동화 확장**

- PR 머지 전 자동 체크
- 심각한 이슈 발견 시 머지 블록
- Slack 알림 연동

<br/>

**1인 프로젝트여도 품질을 포기하지 않는다**

LLM을 활용하면 혼자서도 코드 품질을 유지할 수 있습니다.

완벽한 리뷰는 아니지만, **"아무도 안 봐주는 것"**보다는 **"GPT라도 봐주는 것"**이 훨씬 낫습니다.

앞으로도 1인 프로젝트의 한계를 기술로 극복하는 개발자가 되겠습니다. 🎯

---

**참고 자료:**

**LLM 코드 리뷰:**
- [GPT를 활용한 코드 리뷰 자동화](https://velog.io/@composite/GPT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%BD%94%EB%93%9C%EB%A6%AC%EB%B7%B0-%EC%9E%90%EB%8F%99%ED%99%94)
- [토스 - AI 코드 리뷰 도입기](https://toss.tech/article/ai-code-review)

**GitHub Webhook:**
- [GitHub Webhook으로 자동화하기](https://zzsza.github.io/development/2020/06/06/github-webhook/)
- [Flask로 Webhook 서버 만들기](https://velog.io/@doondoony/python-flask-github-webhook)

**프롬프트 엔지니어링:**
- [효과적인 프롬프트 작성법](https://techblog.woowahan.com/14126/)
- [LLM 프롬프트 최적화 가이드](https://oliveyoung.tech/blog/2024-04-30/prompt-engineering/)

