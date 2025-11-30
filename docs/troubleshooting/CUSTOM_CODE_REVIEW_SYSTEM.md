> 1인 개발의 한계를 극복하기 위한 나만의 해결책

---

## 🎯 들어가며

현재 저는 **Dataracy**라는 데이터 분석 플랫폼의 백엔드를 혼자 개발하고 있습니다. Java Spring Boot 서비스입니다. 이전 프로젝트에서는 팀원들과 활발하게 코드 리뷰를 주고받으며 개발했습니다. 그때는 당연하게 여겼던 것들이, 혼자 하게 되니 얼마나 소중했는지 깨달았습니다.

**"이 코드... 이렇게 짜도 되는 걸까?"**

매일 같은 고민의 반복이었습니다.

---

## 🚨 문제: 코드 리뷰의 부재가 가져온 악순환

### 놓쳐버린 첫 번째 경고 신호

프로젝트 초반, JWT 인증 필터를 구현했습니다:

```java
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtValidateUseCase jwtValidateUseCase;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        // 🚨 토큰이 없어도 검증 시도!
        if (jwtValidateUseCase.validateToken(token)) {
            Authentication auth = jwtValidateUseCase.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 🚨 null 체크 없음!
        return bearerToken.substring(7);  // "Bearer " 제거
    }
}
```

당시에는 "동작하니까 괜찮겠지" 하고 넘어갔습니다. 하지만 이 코드에는 **심각한 문제**가 있었습니다:

- 🚨 **NullPointerException 발생** → Authorization 헤더 없으면 앱 전체 다운
- 🚨 **Public 경로도 필터 적용** → 회원가입, 로그인 API도 토큰 필요하게 됨
- 🚨 **예외 처리 누락** → 잘못된 토큰 형식일 때 `StringIndexOutOfBoundsException`
- 🚨 **에러 메시지 없음** → 사용자는 왜 안 되는지 알 수 없음

이전 프로젝트였다면 동료가 PR 코멘트로 바로 지적했을 문제들입니다. 하지만 혼자 하다 보니, \*\*다시 썼던 코드를 다시 보는 과정에서야 알아챘습니다.

### 악순환의 시작

이런 일이 반복되면서 악순환에 빠졌습니다:

1. 코드를 작성한다
2. "이게 맞나?" 의심하지만 확신할 수 없다
3. 일단 배포한다
4. 문제가 생긴다
5. 뒤늦게 수정한다
6. **같은 실수를 다른 곳에서 또 반복한다**

가장 답답했던 건 **성장하고 있다는 느낌이 들지 않는다는 것**이었습니다. 이전에는 코드 리뷰를 통해 "아, 이렇게 하는 게 더 좋구나" 하고 배웠는데, 이제는 그런 기회가 없었습니다.

---

## 💡 해결 방법 모색: 여러 시도와 실패

### 1. 커뮤니티에 질문하기

국내 개발 커뮤니티에 코드 조각을 올려봤습니다.

**결과:**

- 답변을 받기까지 1-3일 소요
- 프로젝트 맥락을 이해 못 하는 답변이 대부분
- 매번 코드를 올리기도 부담스러움

### 2. 유료 코드 리뷰 서비스

사이드 프로젝트 수준에서 코드 리뷰를 받기 위해 월 3만원을 받는게 맞는 걸까라는 생각이 들어 이건 후순위로 두는 것이 맞다고 판단하였습니다.

### 3. ChatGPT에 수동으로 물어보기

코드를 복사해서 ChatGPT에 붙여넣고 리뷰를 요청했습니다.

**한계:**

- 매번 복사/붙여넣기가 너무 귀찮음
- 프로젝트의 설계 원칙을 매번 설명해야 함
- **귀찮아서 안 하게 됨** ← 이게 제일 큰 문제

### 4. GitHub Copilot을 쓰면 되지 않나?

**한계:**

- PR 단위 리뷰가 아닌 개별 코드 제안만 제공
- 프로젝트 전체 설계 원칙(DDD, AOP 분리 등)을 이해 못 함
- 트랜잭션 범위, 동시성 같은 실무 이슈 지적이 약함
- "이 코드 왜 이렇게 짰는지" 맥락을 모름

### 5. SonarQube 같은 정적 분석 도구는?

실제로 SonarQube를 병행 사용 중입니다.

**차이점:**

- **SonarQube**: 코드 스멜, 중복 코드, 보안 취약점 같은 **규칙 기반** 분석
  - "이 메서드가 너무 깁니다" (정량적)
  - "이 변수가 사용되지 않습니다" (기계적)
- **AI 리뷰**: 설계 관점, 트랜잭션 경계, 책임 분리 같은 **맥락 기반** 피드백
  - "외부 API 호출이 트랜잭션 안에 있으면 문제입니다" (실무적)
  - "이벤트 기반으로 분리하면 더 좋습니다" (설계적)

**깨달음:** 두 개가 상호 보완적입니다. SonarQube는 "문법 선생님", AI는 "설계 멘토"

### 그래서 내린 결론

이 순간 번뜩이는 아이디어가 떠올랐습니다:

> **"GitHub PR을 올리면 자동으로 GPT가 리뷰해주게 만들면 어떨까?"**

제 요구사항은 명확했습니다:

- ✅ PR을 올리면 **자동으로** 리뷰
- ✅ **10분 안에** 피드백
- ✅ 프로젝트 설계 원칙을 **이해하는** 리뷰
- ✅ **지속 가능한** 비용
- ✅ SonarQube와 **보완 관계**

직접 만들기로 결정했습니다.

---

## 🏗️ 설계: 어떻게 만들 것인가

### 핵심 아이디어

GitHub PR이 올라가면 → Webhook으로 알림 받고 → GPT에게 리뷰 요청 → 결과를 PR에 코멘트로 등록

간단하죠? 하지만 실제로 구현하려니 고민할 것들이 많았습니다.

### 기술 스택 선정

**1. GPT-4o (OpenAI API)**

- GPT-4o: "트랜잭션 범위가 부적절합니다. 외부 API 호출을 분리하세요" ← 실무적

**리뷰 품질 차이를 고려해** GPT-4o로 결정.

**2. Flask (Python)**

가벼운 웹훅 서버가 필요했습니다. Spring Boot는 오버스펙이고, Express는 비동기 처리가 복잡할 것 같았습니다. Flask는 딱 웹훅 받고 GPT 호출하기에 적합했습니다.

**3. Docker**

EC2에 바로 배포하지 않고 Docker로 격리했습니다. 메인 Spring 애플리케이션과 독립적으로 운영하고 싶었습니다.

### 전체 아키텍처

시스템의 전체 구조를 도식화하면 이렇습니다:

```
┌─────────────────────────────────────────────────────────────┐
│                        GitHub PR                            │
│                     (Pull Request Opened)                   │
└─────────────────────┬───────────────────────────────────────┘
                      │ Webhook Event
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              Flask Webhook Server (Docker)                  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 1. Signature Verification (HMAC-SHA256)               │  │
│  │    → 보안 검증: GitHub에서 온 요청인가?                     │  │
│  └───────────────────────────────────────────────────────┘  │
│                         │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 2. Diff Download (GitHub API)                         │  │
│  │    → PR의 변경사항(diff) 다운로드                          │  │
│  └───────────────────────────────────────────────────────┘  │
│                         │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 3. Diff Parsing (정규식)                                │  │
│  │    → 파일별로 변경사항 분리                                 │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────┘
                      │ Parsed Files
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              GPT-4o API (OpenAI)                            │
│                                                             │
│  ┌──────────────┐        ┌──────────────┐                   │
│  │  전체 PR 요약   │       │ 파일별 리뷰     │                   │
│  │  (1회 호출)    │        │  (N회 호출)    │                  │
│  └──────────────┘        └──────────────┘                   │
│         │                        │                          │
│         └────────┬───────────────┘                          │
│                  │ AI Review Results                        │
└──────────────────┴──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│            GitHub API (Comment Posting)                     │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 배치 처리: 파일별로 코멘트 그룹화                             │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│               PR에 자동 코멘트 등록 완료 ✅                       │
└─────────────────────────────────────────────────────────────┘
```

**데이터 흐름:**

1. PR Open → Webhook Event (JSON)
2. Diff 다운로드 → Text (수천 라인)
3. 파일별 파싱 → List[{path, content}]
4. GPT 리뷰 → Markdown 텍스트
5. GitHub 코멘트 → PR에 표시

정말 단순합니다. **하지만 디테일을 봐야합니다.**

---

## 🔨 구현: 실제로 만들어보기

### 1단계: GitHub Webhook 받기

제일 먼저 필요한 건 GitHub PR 이벤트를 받는 서버였습니다. Flask로 웹훅 엔드포인트를 만들었습니다.

**전체 webhook.py 구조:**

```python
import os
import hmac
import hashlib
import requests
from flask import Flask, request, abort
from reviewer import generate_review_comments
from prompt_summary import build_summary_prompt
from utils import call_gpt

app = Flask(__name__)

# 환경변수 로드
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
GITHUB_REPO = os.getenv("GITHUB_REPO")
GITHUB_SECRET = os.getenv("GITHUB_SECRET")

def verify_signature(payload: bytes, signature: str) -> bool:
    """GitHub Webhook Signature 검증 (보안)"""
    mac = hmac.new(GITHUB_SECRET.encode(), msg=payload, digestmod=hashlib.sha256)
    expected = 'sha256=' + mac.hexdigest()
    return hmac.compare_digest(expected, signature)

@app.route("/webhook", methods=["POST"])
def webhook():
    # 🔐 1. 보안 검증
    payload = request.get_data()  # ⚠️ 중요: JSON 파싱 전 raw bytes
    signature = request.headers.get("X-Hub-Signature-256")

    if not verify_signature(payload, signature):
        abort(400, "Invalid signature")

    # 📦 2. PR 이벤트만 처리
    event = request.headers.get("X-GitHub-Event")
    if event != "pull_request":
        return "Ignored", 200

    data = request.json
    action = data.get("action")

    # PR open 시에만 리뷰
    if action != "opened":
        return "Ignored", 200

    # 📑 3. Diff 수집
    pr_number = data["pull_request"]["number"]
    diff_url = data["pull_request"]["diff_url"]
    diff_text = requests.get(diff_url).text

    # ✨ 4. 전체 요약 생성
    summary_prompt = build_summary_prompt(diff_text)
    summary_response = call_gpt(summary_prompt).strip()
    post_github_comment(pr_number, f"🚀 **GPT PR 전체 요약**\n\n{summary_response}")

    # ✨ 5. 파일별 상세 리뷰 생성
    review_comments = generate_review_comments(diff_text)
    for comment in review_comments:
        post_github_comment(pr_number, comment["body"])

    return "Review posted", 200

def post_github_comment(pr_number: int, body: str):
    """GitHub PR에 코멘트 등록"""
    url = f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments"
    requests.post(
        url,
        headers={
            "Authorization": f"Bearer {GITHUB_TOKEN}",
            "Accept": "application/vnd.github+json",
        },
        json={"body": body}
    )

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
```

**핵심 포인트:**

1. **보안 검증 (HMAC-SHA256)**: 누구나 요청을 보낼 수 있는 공개 엔드포인트이므로 필수
2. **Raw bytes 사용**: JSON 파싱 전 원본 바이트로 서명 검증
3. **이벤트 필터링**: PR open 이벤트만 처리 (synchronize, close 등은 무시)
4. **단순한 흐름**: 요약 → 상세 리뷰 → 코멘트 등록

처음에 `request.json`을 먼저 파싱하고 검증했다가 계속 실패했습니다. JSON 파싱 시 키 순서가 바뀌면서 서명이 달라진 것입니다. **Raw bytes를 그대로 써야 합니다.**

### 2단계: Git Diff 파싱

GitHub API에서 받은 diff는 이런 형식입니다:

```
diff --git a/src/main/java/UserService.java b/src/main/java/UserService.java
index abc123..def456 100644
--- a/src/main/java/UserService.java
+++ b/src/main/java/UserService.java
@@ -10,5 +10,8 @@
 context line
-removed line
+added line
```

이걸 파일별로 나눠야 GPT에게 각 파일마다 리뷰를 요청할 수 있습니다. 정규식으로 파싱했습니다:

```python
import re

def extract_changed_files(diff_text: str) -> list[dict]:
    # "diff --git a/경로" 패턴으로 파일 시작 지점 찾기
    pattern = re.compile(r"^diff --git a/(.+?) b/.*?$", re.MULTILINE)
    matches = pattern.finditer(diff_text)

    files = []
    indices = [match.start() for match in matches] + [len(diff_text)]

    for i in range(len(indices) - 1):
        segment = diff_text[indices[i]:indices[i + 1]]
        # 파일 경로 추출
        path_match = re.search(r"^diff --git a/(.+?) b/", segment)
        if path_match:
            files.append({
                "path": path_match.group(1),
                "content": segment
            })

    return files
```

**여기서도 함정:** 처음에 정규식을 `r"^diff --git a/(.+)$"`로 썼다가 `b/경로`까지 포함되어 파일 경로가 중복됐습니다. **Non-greedy 매칭(`?`)**이 핵심이었습니다.

**실제 reviewer.py 구조:**

```python
from prompt_file_review import build_file_review_prompt
from diff_parser import extract_changed_files
from utils import call_gpt
import re

def generate_review_comments(diff_text: str) -> list[dict]:
    """파일별 리뷰 코멘트 생성"""
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

이렇게 파일별로 독립적으로 리뷰를 받으면, 각 파일의 맥락을 정확히 전달할 수 있습니다.

### 3단계: 프롬프트 엔지니어링 (핵심!)

이 부분이 **가장 중요하고, 가장 많은 시행착오**를 겪었습니다.

처음 만든 프롬프트는 이랬습니다:

```
아래 코드를 리뷰해주세요.
{코드}
```

GPT 응답:

```
1. 변수명을 더 명확하게 하세요
2. 주석을 추가하세요
3. 들여쓰기를 일관되게 하세요
```

**완전히 쓸모없는 리뷰**였습니다. 형식적이고, 실무에 도움이 안 되는 내용뿐이었죠.

여러 번의 시도 끝에 깨달은 것:

1. **프로젝트 맥락을 알려줘야 한다**
2. **원하는 리뷰 유형을 구체적으로 지정해야 한다**
3. **원하지 않는 것도 명확히 말해야 한다**

최종 프롬프트는 이렇게 발전했습니다:

```python
def build_file_review_prompt(file_diff: str) -> str:
    return f"""
당신은 Java + Spring 기반 SaaS 백엔드 실무 코드 리뷰 전문가입니다.

📌 프로젝트 컨텍스트:
- Java 17, Spring Boot 3.5 기반 실무 프로젝트
- DDD 구조, AOP 기반 인증/검증, CustomException 사용
- Redis Lock, Kafka DLQ, SoftDelete 정책 준수 필요

🎯 리뷰 목적:
**형식적인 스타일 지적은 생략하고**, 실무에서 문제가 될 수 있는 것만 지적하세요.

✅ 리뷰해야 할 것:
- 트랜잭션 범위가 부적절한 경우
- 동시성 문제 가능성
- 외부 API 호출이 잘못된 위치에 있는 경우
- AOP로 분리해야 할 횡단 관심사
- 예외 처리 누락

❌ 리뷰하지 말 것:
- 변수명, 메서드명 (충분히 명확하면 OK)
- 주석 (필수가 아니면 언급하지 마세요)
- 코드 스타일, 포맷팅

출력 형식:
🚀 [문제 요약]
- 어떤 코드가 문제인지
- 왜 문제인지
- 개선 방안

{file_diff}
"""
```

이렇게 바꾸니 **완전히 다른 수준의 리뷰**가 나왔습니다:

```
🚀 트랜잭션 범위 문제
- `updateUserProfile()` 메서드에서 이메일 발송이 트랜잭션 안에 있습니다
- 외부 API 지연 시 DB 커넥션을 장시간 점유하게 됩니다
- 트랜잭션 커밋 후 이메일을 발송하거나, 이벤트 기반으로 분리하세요

🚀 동시성 문제 가능성
- 캐시 무효화가 트랜잭션 내부에 있어, 롤백 시에도 캐시가 삭제됩니다
- TransactionalEventListener로 커밋 후 캐시를 무효화하세요
```

**바로 이거였습니다!** 제가 원했던 리뷰!

### 4단계: GPT API 호출

OpenAI SDK를 사용해서 간단하게 구현했습니다:

```python
from openai import OpenAI
import os

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def call_gpt(prompt: str) -> str:
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content
```

간단하고 명확합니다. OpenAI SDK가 기본적인 에러 핸들링을 해주므로, 복잡한 재시도 로직 없이도 안정적으로 동작합니다.

### 5단계: GitHub에 코멘트 등록

리뷰 결과를 PR에 코멘트로 등록하는 부분입니다:

```python
import requests

def post_github_comment(pr_number: int, body: str):
    url = f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments"
    requests.post(
        url,
        headers={
            "Authorization": f"Bearer {GITHUB_TOKEN}",
            "Accept": "application/vnd.github+json",
        },
        json={"body": body}
    )
```

**여기서 큰 문제 발생:** 파일이 10개면 10번 API를 호출하는데, GitHub의 secondary rate limit에 걸렸습니다.

해결책: **파일별로 코멘트를 하나로 합쳐서** API 호출을 줄였습니다. 10개 파일 → 10번 호출에서 1-2번 호출로 줄이니 문제가 해결됐습니다.

### 6단계: Docker 배포

시스템을 독립적으로 운영하기 위해 Docker를 사용했습니다.
저는 도커를 사용해 ec2의 백그라운드 계속해서 실행시켜두었습니다.

**Dockerfile:**

```dockerfile
FROM python:3.11-slim

WORKDIR /app

# 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 코드 복사
COPY . .

EXPOSE 8000

CMD ["python", "webhook.py"]
```

**docker-compose.yml:**

```yaml
version: "3.8"

services:
  codereview-llm:
    container_name: codereview-llm
    build:
      context: .
    ports:
      - "8000:8000"
    env_file:
      - .env # 환경변수 파일
    restart: always
    command: python webhook.py
```

**requirements.txt:**

```
flask
openai
requests
python-dotenv
```

**환경변수 (.env):**

```bash
GITHUB_TOKEN=ghp_xxxxxxxxxxxxx
GITHUB_REPO=username/dataracy
GITHUB_SECRET=your_webhook_secret_key
OPENAI_API_KEY=sk-xxxxxxxxxxxxx
```

**배포 명령:**

```bash
# Docker 이미지 빌드
docker-compose build

# 컨테이너 실행
docker-compose up -d

# 로그 확인
docker logs -f codereview-llm
```

이제 EC2나 로컬 서버에서 간단하게 실행할 수 있습니다. GitHub Webhook 설정에서 `http://your-server:8000/webhook`을 등록하면 끝!

---

## 🔥 트러블 슈팅: 실전에서 마주한 문제들

### 문제 1: GitHub Webhook 서명 검증 실패

**상황:**

서버를 띄우고 GitHub Webhook을 설정했는데, 모든 요청이 `400 Invalid signature`로 거부됐습니다.

```bash
POST /webhook HTTP/1.1
X-Hub-Signature-256: sha256=abc123...
X-GitHub-Event: pull_request

❌ 400 Bad Request: Invalid signature
```

분명 Secret을 똑같이 설정했는데 왜 안 되는지 한참을 헤맸습니다.

**원인:**

처음에는 이렇게 구현했습니다:

```python
@app.route("/webhook", methods=["POST"])
def webhook():
    data = request.json  # ❌ 먼저 JSON 파싱
    signature = request.headers.get("X-Hub-Signature-256")

    # JSON을 다시 문자열로 변환해서 검증
    payload = json.dumps(data).encode()
    mac = hmac.new(GITHUB_SECRET.encode(), payload, hashlib.sha256)

    if 'sha256=' + mac.hexdigest() != signature:
        abort(400, "Invalid signature")
```

문제는 **JSON 파싱 후 다시 문자열로 만들면 키 순서가 바뀔 수 있다**는 것이었습니다!

**해결:**

```python
@app.route("/webhook", methods=["POST"])
def webhook():
    # ✅ JSON 파싱 전에 raw bytes 추출
    payload = request.get_data()
    signature = request.headers.get("X-Hub-Signature-256")

    mac = hmac.new(GITHUB_SECRET.encode(), msg=payload, digestmod=hashlib.sha256)
    expected = 'sha256=' + mac.hexdigest()

    if not hmac.compare_digest(expected, signature):
        abort(400, "Invalid signature")

    # 검증 통과 후 JSON 파싱
    data = request.json
```

**교훈:** HMAC 검증은 **원본 바이트를 그대로** 사용해야 합니다. 한 번이라도 변환하면 서명이 달라집니다.

### 문제 2: Git Diff 파일 경로 중복

**상황:**

파일별로 리뷰를 나눠서 코멘트를 등록하려고 했는데, 파일 경로가 이상하게 추출됐습니다:

```python
# 기대: "src/main/java/UserService.java"
# 실제: "src/main/java/UserService.java b/src/main/java/UserService.java"
```

**원인:**

Git diff 헤더 형식이 이렇습니다:

```
diff --git a/경로 b/경로
```

처음 정규식을 이렇게 짰습니다:

```python
# ❌ 잘못된 정규식
pattern = re.compile(r"^diff --git a/(.+)$", re.MULTILINE)
# 결과: "경로 b/경로" 전체가 캡처됨
```

**해결:**

```python
# ✅ Non-greedy 매칭 사용
pattern = re.compile(r"^diff --git a/(.+?) b/.*?$", re.MULTILINE)
#                                      ^^^ 여기가 핵심!
```

`(.+?)`의 `?`가 **non-greedy 매칭**을 의미해서, `b/` 앞까지만 캡처합니다.

**교훈:** 정규식에서 `.+`는 욕심쟁이입니다. `.+?`로 최소 매칭을 써야 할 때가 많습니다.

### 문제 3: GPT 응답 형식 불일치

**상황:**

프롬프트에 "🚀 이모지로 시작하세요"라고 했는데:

- 어떤 때는 `🚀 제목` (공백 있음)
- 어떤 때는 `🚀제목` (공백 없음)
- 어떤 때는 아예 무시하고 일반 텍스트만

코멘트를 `🚀` 기준으로 split하는데 파싱이 계속 깨졌습니다.

**해결:**

```python
# 유연한 파싱
review_blocks = re.split(r"\n?🚀", gpt_response)
#                         ^^^ 앞의 개행도 포함 가능

for block in review_blocks:
    block = block.strip()  # 앞뒤 공백 제거
    if not block or len(block) < 10:  # 너무 짧은 블럭 제외
        continue

    comments.append({
        "body": f"[🚀 `{path}` 파일 리뷰]\n\n💬 {block}"
    })
```

**교훈:** LLM 출력은 **항상 불확실**합니다. 프롬프트를 아무리 명확하게 써도 100% 일관된 응답은 불가능합니다. 파싱 로직을 유연하게 만들어야 합니다.

### 문제 4: GitHub API Rate Limit 초과

**상황:**

파일이 많은 PR을 올리면 이런 에러가 발생했습니다:

```bash
POST https://api.github.com/repos/.../comments  # OK
POST https://api.github.com/repos/.../comments  # OK
POST https://api.github.com/repos/.../comments  # OK
...
POST https://api.github.com/repos/.../comments  # ❌ 403 Forbidden

{
  "message": "You have exceeded a secondary rate limit"
}
```

파일 10개면 코멘트 10개 → API 10번 호출 → Secondary Rate Limit 발동!

**원인:**

GitHub는 공식 Rate Limit (5,000 req/hour) 외에도, **단시간 내 동일 엔드포인트 연속 호출을 제한**합니다. 문서에 명시되지 않은 숨겨진 제한이었죠.

**해결:**

처음에는 `time.sleep(1)` 추가했는데, 코멘트 20개면 20초 대기... 너무 느렸습니다.

최종 해결책: **파일별로 코멘트를 하나로 합치기**

```python
# Before: 파일 10개 → API 10번 호출
for comment in review_comments:
    post_github_comment(pr_number, comment["body"])

# After: 파일 10개 → API 1번 호출
all_reviews = "\n\n---\n\n".join([c["body"] for c in review_comments])
post_github_comment(pr_number, all_reviews)
```

**결과:**

- 10개 파일 → 10번 호출에서 1번 호출로
- Rate Limit 문제 완전 해결
- 보너스: PR 코멘트가 더 깔끔하게 정리됨

**교훈:** API를 설계할 때는 항상 **Rate Limit을 고려**해야 합니다. 배치 처리로 호출 횟수를 줄이는 게 핵심입니다.

### 문제 5: 비용 관리의 필요성

**상황:**

2주 운영 후 OpenAI 청구서를 봤습니다: **$47**

계산해보니:

- PR 15개
- 파일당 평균 2,500 토큰 소비
- 월 100개 PR 예상 시 → **월 $80-100**

지속하기엔 부담스러운 비용이었습니다.

**앞으로 개선할 부분:**

1. **불필요한 파일 제외**: gradle, xml, md 같은 설정/문서 파일
2. **Diff 압축**: context 라인 최소화로 토큰 절감
3. **캐싱**: 동일한 diff 재리뷰 방지

현재는 기본 버전을 운영 중이고, 비용이 더 부담되면 이런 최적화를 추가할 계획입니다.

---

## 🎉 결과: 실제로 얼마나 도움이 됐을까?

### 정량적 효과 (8주 운영 데이터)

```
총 PR 수: 87개
자동 리뷰 성공: 85개 (97.7%)
평균 리뷰 시간: 2분 43초
발견한 실무 이슈: 60+ 건
월 운영 비용: $45-55 (PR 개수에 따라 변동)
```

**발견한 주요 이슈들:**

- **동시성 버그 7건**: 조회수, 좋아요 카운트에서 Race Condition
- **N+1 문제 12건**: Lazy Loading으로 인한 성능 이슈
- **트랜잭션 범위 문제 23건**: 외부 API가 트랜잭션 안에 있는 경우
- **예외 처리 누락 18건**: try-catch 없이 외부 API 호출
- **보안 취약점 3건**: 인증 검증 누락, SQL Injection 가능성

**만약 리뷰 시스템이 없었다면?** 이 문제들을 프로덕션에서 겪었을 겁니다.

### 실제 개선 사례: N+1 문제 해결

AI 리뷰가 지적한 N+1 문제를 실제로 개선한 사례입니다.

**문제 상황:**

인기 프로젝트 조회 API에서 N+1 문제 발견:

- 평균 응답시간: **139.23ms**
- 최대 응답시간: **823.68ms** (거의 1초!)
- DB 쿼리 수: **31개** (프로젝트 5개 조회 시)
- 총 요청 수: 104건

**AI 리뷰 지적:**

```markdown
⚠️ N+1 문제: 라벨 매핑에서 반복 쿼리 발생

프로젝트 5개를 조회하면:

- 메인 쿼리: 1개
- 각 프로젝트의 라벨 조회: 6개 × 5 = 30개
  → 총 31개 쿼리 발생!

조회 개수가 10개면 61개, 20개면 121개 쿼리로 증가합니다.
배치 쿼리로 한 번에 가져오세요.
```

**개선 결과 (2단계 최적화):**

| 단계              | 평균 응답시간 | 95% 응답시간 | 최대 응답시간 | DB 쿼리 수 | 총 요청 수 | 개선율 (누적) |
| ----------------- | ------------- | ------------ | ------------- | ---------- | ---------- | ------------- |
| **N+1 개선 전**   | 139.23ms      | 245.12ms     | 823.68ms      | 31개       | 104건      | -             |
| **N+1 개선 후**   | 48.92ms       | 89.46ms      | 267.89ms      | 7개        | 167건      | **64.9% ↑**   |
| **캐싱 - 미스**   | 50.78ms       | 92.35ms      | 284.57ms      | 7개        | 163건      | 63.5% ↑       |
| **캐싱 - 히트**   | **9.18ms**    | **12.35ms**  | **15.72ms**   | **0개**    | **228건**  | **93.4% ↑**   |
| **Load (10 VUs)** | 10.92ms       | 18.35ms      | 42.68ms       | 0개        | 4,468건    | 92.2% ↑       |

**핵심 개선 지표:**

```
🚀 응답시간: 93.4% 개선 (139.23ms → 9.18ms)
   └─ 15배 빨라짐

📉 최대 응답시간: 98.1% 개선 (823.68ms → 15.72ms)
   └─ 52배 빨라짐

💾 DB 쿼리: 100% 제거 (31개 → 0개)
   └─ 캐시 히트 시

📊 처리량: 119% 증가 (104건 → 228건)
   └─ Smoke test 기준

📈 확장성: 현재 트래픽의 20배 이상 처리 가능
   └─ Load test 4,468건 처리
```

**결과:**

거의 1초 걸리던 요청이 **10ms 이하로 단축**되어 사용자 경험이 극적으로 개선됐습니다. 이게 바로 AI 코드 리뷰의 실질적인 가치입니다.

![성능 개선 결과](./images/performance-improvement.png)

### 실제 개선 사례: Rate Limiting으로 무차별 대입 공격 방어

AI 리뷰가 지적한 보안 취약점을 실제로 개선한 사례입니다.

**문제 상황:**

로그인 API에서 무차별 대입 공격에 대한 방어 메커니즘 부재:

- k6 공격 시뮬레이션에서 **638회 시도 중 8개 계정 뚫림** (약 1.3% 성공률)
- 시도 횟수 제한 없음 → 시간이 지날수록 더 많은 계정 탈취 가능
- 모든 시도에 BCrypt 검증 수행 → 서버 리소스 낭비
- 평균 응답시간: **120ms** (모든 요청에 ~100ms BCrypt 검증)

**AI 리뷰 지적:**

```markdown
⚠️ Rate Limiting 고려 필요 (무차별 토큰 검증 시도 방지)

현재 로그인 API:

- 시도 횟수 제한 없음
- 공격자가 무한정 시도 가능
- 약한 비밀번호("password", "123456")는 결국 뚫림

권장 사항:

1. email:IP 조합으로 Rate Limiting 적용
2. 각 계정당 1분에 5회 제한
3. BCrypt 전에 빠른 차단으로 서버 리소스 절약
```

**개선 과정 (Memory → Redis 2단계 최적화):**

| 단계               | 평균 응답시간 | 최대 응답시간 | BCrypt 검증 | 429 차단 | 공격 성공 | 총 요청 수 |
| ------------------ | ------------- | ------------- | ----------- | -------- | --------- | ---------- |
| **Before**         | 120ms         | 246ms         | 638회       | 0개      | **8개**   | 638개      |
| **After (Memory)** | 37.24ms       | 152ms         | 248회       | 571개    | **0개**   | 819개      |
| **After (Redis)**  | 39.12ms       | 163ms         | 251회       | 552개    | **0개**   | 803개      |

**핵심 개선 지표:**

```
🛡️ 공격 성공: 100% 차단 (8개 → 0개)
   └─ 각 계정당 5회/분 제한

⚡ 응답시간: 67% 개선 (120ms → 39.12ms)
   └─ 약 68%의 요청을 빠르게 차단 (~2ms)

💾 BCrypt 검증: 61% 감소 (638회 → 251회)
   └─ 비용 높은 연산 전에 빠른 차단

🚫 차단 효과: 552회 차단 (68.7%)
   └─ 서버 리소스 보호

📈 처리량: 26% 증가 (638개 → 803개)
   └─ 응답 빨라져 동일 시간에 더 많이 처리
```

**왜 Redis를 선택했나:**

1. **분산 환경 대응**: 여러 서버에서 공유 카운터 사용
2. **영속성**: 서버 재시작해도 카운터 유지
3. **자동 TTL 관리**: 메모리 누적 방지
4. **원자적 연산**: INCR 명령으로 동시성 안전

**구현 핵심:**

```java
// Redis Rate Limiting Adapter
@Override
public boolean isAllowed(String key, int maxRequests, int windowMinutes) {
    String redisKey = "rate_limit:" + key;

    // ✅ Redis INCR: 원자적 증가 (분산 환경에서도 안전)
    Long count = redisTemplate.opsForValue().increment(redisKey, 1);

    // 첫 요청이면 TTL 설정 (1분 후 자동 삭제)
    if (count == 1) {
        redisTemplate.expire(redisKey, windowMinutes, TimeUnit.MINUTES);
    }

    return count <= maxRequests;
}

// Service Layer
public RefreshTokenResponse loginWithRateLimit(
        SelfLoginRequest requestDto, String clientIp) {

    // 1. Rate Limiting 먼저 확인 (~2ms)
    String rateLimitKey = requestDto.email() + ":" + clientIp;
    if (!rateLimitPort.isAllowed(rateLimitKey, 5, 1)) {
        throw new AuthException(AuthErrorStatus.RATE_LIMIT_EXCEEDED);  // 429
    }

    // 2. 비밀번호 검증 (Rate Limit 통과한 요청만, ~100ms)
    UserInfo userInfo = checkLoginPossibleAndGetUserInfo(...);

    // 3. JWT 토큰 생성 및 반환
    return generateRefreshTokenResponse(userInfo);
}
```

**결과:**

무차별 대입 공격을 **100% 차단**하면서도 서버 리소스를 **61% 절약**하고, 응답 속도는 **67% 개선**되었습니다. 보안과 성능을 동시에 잡은 사례입니다.

**트러블 슈팅 상세 문서:**  
[Rate Limiting으로 무차별 대입 공격 방어하기](https://velog.io/@juuuunny/%ED%95%98%EB%A3%A8%EC%97%90-10%EB%A7%8C-%EB%B2%88-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%8B%9C%EB%8F%84%EC%9A%94-Rate-Limiting%EC%9C%BC%EB%A1%9C-%EB%AC%B4%EC%B0%A8%EB%B3%84-%EB%8C%80%EC%9E%85-%EA%B3%B5%EA%B2%A9-%EB%A7%89%EA%B8%B0)

### 실제 리뷰 예시

PR을 올리면 자동으로 이런 코멘트가 달립니다:

**전체 요약 코멘트:**

```markdown
🚀 **GPT PR 전체 요약**

### 주요 변경 사항

- JWT 필터에 null 체크 및 예외 처리 추가
- shouldNotFilter()로 Public 경로 필터 제외
- 401 에러 시 명확한 JSON 응답 제공

### 설계 정책 준수 여부

✅ 안전한 null 체크 구현
✅ Public 경로 필터 제외
✅ 명확한 에러 응답
⚠️ 로그 기록 추가 권장 (보안 모니터링)
⚠️ Rate Limiting 고려 필요 (무차별 토큰 검증 시도 방지)

### 리팩토링 제안

- 인증 실패 시 로그 기록 (IP, 요청 경로 포함)
- 토큰 검증 실패 카운트 추적 (Redis)
- SecurityPathConfig 분리로 경로 관리 중앙화
```

**파일별 상세 리뷰:**

````markdown
[🚀 `JwtFilter.java` 파일 리뷰]

💬 NPE 방지는 개선됐으나 보안 강화 필요

현재 코드에서 null 체크는 개선됐으나, 추가 보안 고려사항이 있습니다:

​```java
@Override
protected void doFilterInternal(HttpServletRequest request,
HttpServletResponse response,
FilterChain filterChain) throws ServletException, IOException {
try {
String token = extractToken(request);

        if (token != null && jwtValidateUseCase.validateToken(token)) {
            Authentication auth = jwtValidateUseCase.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);

    } catch (JwtException e) {
        // 🚨 로그가 없어 공격 시도 추적 불가
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}");
    }

}
​```

**추가 보안 강화:**

1. **로그 기록 추가**
   - 인증 실패 시도 추적
   - IP, User-Agent, 요청 경로 기록

​```java
} catch (JwtException e) {
String clientIp = getClientIp(request);
log.warn("JWT 인증 실패 - IP: {}, Path: {}, Reason: {}",
clientIp, request.getRequestURI(), e.getMessage());

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}");
    return;  // 필터 체인 중단!

}
​```

2. **Rate Limiting 고려**
   - 동일 IP에서 연속 실패 시 차단
   - Redis로 실패 카운트 관리

​```java
private void recordFailedAttempt(String clientIp) {
String key = "auth:fail:" + clientIp;
Long failCount = redisTemplate.opsForValue().increment(key);
redisTemplate.expire(key, 5, TimeUnit.MINUTES);

    if (failCount > 10) {
        throw new TooManyAuthAttemptsException();
    }

}
​```

3. **SecurityContext 클리어**

​`java
} catch (JwtException e) {
    SecurityContextHolder.clearContext();  // 추가!
    // ... 에러 응답
}
​`

**주의사항:**

- 예외 발생 후 `return`을 명시해야 필터 체인이 중단됩니다
- 그렇지 않으면 인증 실패해도 요청이 계속 진행될 수 있습니다
````

이런 식으로 **구체적인 문제 + 개선 방안**을 제시합니다.

### 프로젝트 품질 지표 개선

시스템 도입 전후를 비교하면 명확한 차이가 보입니다.

**Before (리뷰 시스템 도입 전 - 3개월):**

- 프로덕션 버그: 월 3-4건 발생
- 긴급 핫픽스 배포: 월 2-3회
- 리팩토링 주기: 불규칙 (주관적 판단으로 미루다가 몰아서)
- 코드 리뷰: 없음
- 설계 결정 기록: 없음 (머릿속에만)

**After (8주 운영 후):**

- 프로덕션 버그: 월 0-1건 (**83% 감소**)
- 긴급 핫픽스 배포: 0회
- 리팩토링 주기: 즉시 (피드백 받자마자 개선)
- 코드 리뷰: 87개 PR 자동 리뷰
- 설계 결정 기록: PR 코멘트에 자동 문서화

**코드 품질 도구 병행 사용 효과:**

```
SonarQube (정적 분석)
├─ Quality Gate: A등급 유지
├─ Code Smells: 0건
├─ Technical Debt Ratio: < 5%
└─ Security Hotspots: 정기 검토

JaCoCo (테스트 커버리지)
├─ Line Coverage: 평균 78%
├─ Branch Coverage: 평균 71%
└─ AI 리뷰가 "테스트 추가하세요" 권장 → 자연스럽게 상승

CheckStyle + Spotless (코드 스타일)
└─ 형식은 자동화, AI는 설계에 집중
```

**개발 속도 개선:**

| 작업               | Before               | After            | 개선율   |
| ------------------ | -------------------- | ---------------- | -------- |
| 리뷰 대기 시간     | N/A (없음)           | 2분 43초         | -        |
| 리팩토링 결정 시간 | 평균 2시간           | 평균 15분        | **87%↓** |
| 버그 수정 사이클   | 2일 (발견→수정→배포) | 당일 (사전 발견) | **75%↓** |
| 설계 고민 시간     | 혼자 고민 30분+      | 피드백 확인 5분  | **83%↓** |

### 정성적 효과: 나의 변화

**Before: 불안한 개발**

```java
// JWT 필터... 이렇게 짜긴 했는데 괜찮을까?
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        // 토큰 검증... null이면 어떻게 되지?
        if (jwtValidateUseCase.validateToken(token)) {
            Authentication auth = jwtValidateUseCase.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // null 체크... 필요한가?
        return bearerToken.substring(7);
    }
}
```

**AI 리뷰:**

```
🚨 심각한 NPE 위험: Authorization 헤더 null 체크 필수!

현재 문제:
1. Authorization 헤더 없는 요청 → bearerToken이 null
2. null.substring(7) 호출 → NullPointerException 발생
3. 필터에서 예외 발생 → 전체 요청 실패 (500 에러)

영향 범위:
- /swagger-ui, /health 같은 public 경로도 필터 통과
- 헤더 없으면 모든 요청 실패
- 사용자는 "Internal Server Error"만 보고 원인 파악 불가

⚠️ Public 경로 제외 필요

필터는 모든 요청에 적용되므로, public 경로는 건너뛰어야 합니다.
shouldNotFilter() 메서드를 오버라이드하거나,
경로 체크 로직을 추가하세요.

개선 방안:
1. null 체크 + 토큰 형식 검증
2. Public 경로 필터 제외
3. 예외 발생 시 적절한 401 응답
```

**After: 확신 있는 개발**

```java
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtValidateUseCase jwtValidateUseCase;
    private final SecurityPathConfig securityPathConfig;

    // ✅ Public 경로는 필터 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return securityPathConfig.isPublicPath(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // ✅ 안전한 토큰 추출
            String token = extractToken(request);

            if (token != null && jwtValidateUseCase.validateToken(token)) {
                Authentication auth = jwtValidateUseCase.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            // ✅ 명확한 에러 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                "{\"error\": \"유효하지 않은 토큰입니다.\"}"
            );
        }
    }

    // ✅ 안전한 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
```

**이제는 "왜 이렇게 구현했는지" 설명할 수 있습니다.**

> "필터는 모든 HTTP 요청에 적용되므로, null 체크가 필수입니다.  
> Public 경로(/swagger, /login 등)는 shouldNotFilter()로 필터를 건너뛰어야 합니다.
>
> 예외 발생 시 필터 체인이 중단되지 않도록 try-catch로 감싸고,  
> 사용자에게 명확한 401 응답과 에러 메시지를 전달해야 합니다.  
> 이렇게 해야 Swagger 문서도 정상적으로 조회할 수 있습니다."

### 가장 큰 변화: 학습 효과

매 PR마다 리뷰를 받으니, 자연스럽게 학습이 됐습니다:

- **트랜잭션 경계**를 의식하게 됨
- **AOP로 분리할 수 있는 것들**이 보임
- **동시성 문제**를 미리 고민하게 됨
- **예외 처리**를 더 촘촘하게 함

8주 전과 지금의 코드를 비교하면, 확실히 품질이 올라갔습니다.

---

## 🤔 회고: 잘한 것과 아쉬운 것

### 잘한 점

**1. 문제를 명확히 정의했다**

단순히 "코드 리뷰가 없다"가 아니라 "지속 가능하고 즉각적인 피드백 시스템이 필요하다"로 구체화한 게 좋았습니다.

**2. MVP를 빠르게 만들었다**

완벽한 시스템을 설계하려 하지 않고, 핵심 기능만 구현해서 빠르게 검증했습니다. 1주일 만에 첫 버전을 만들어서 사용하기 시작했고, 사용하면서 개선했습니다.

**3. 프롬프트 엔지니어링에 집중했다**

기술 스택은 간단했지만, **프롬프트를 어떻게 짜느냐가 전부**였습니다. 여기에 시간을 많이 쓴 게 정답이었습니다.

### 아쉬운 점

**1. 비용 최적화를 나중에 했다**

처음부터 토큰 사용량을 고려했으면 초기 비용 폭탄을 피할 수 있었을 겁니다.

**2. 테스트 코드 리뷰를 제외했다**

현재는 프로덕션 코드만 리뷰합니다. 하지만 테스트 코드의 품질도 중요하다는 걸 나중에 깨달았습니다. (테스트가 깨지기 쉽게 짜여있다던가...)

**3. 리뷰 이력 관리 부족**

어떤 지적이 많았는지, 시간이 지나며 개선됐는지 통계를 수집하지 않았습니다. 성장을 정량화하기 어려워 아쉽습니다.

---

## 💼 비즈니스 임팩트

기술적 성과를 넘어, 실제 프로젝트와 개인 성장에 미친 영향을 정리해봤습니다.

### 개발 생산성 향상

**시간 효율:**

```
리뷰 대기 시간: 없음 (즉시)
  └─ 이전: 커뮤니티 답변 1-3일 대기
  └─ 현재: PR 올리면 2분 43초 내 피드백

리팩토링 결정 시간: 2시간 → 15분
  └─ 이전: "이렇게 해도 될까?" 혼자 고민
  └─ 현재: AI가 구체적 개선안 제시

버그 수정 사이클: 2일 → 당일
  └─ 이전: 프로덕션 발견 → 분석 → 수정 → 배포
  └─ 현재: PR 단계에서 사전 발견 → 즉시 수정

코드 품질 유지 비용: 주 8시간 → 1시간
  └─ 이전: 수동 검토, 긴급 버그 수정
  └─ 현재: 자동 리뷰, 사전 예방
```

**코드 품질 일관성:**

- **설계 원칙 자동 체크**: 트랜잭션, AOP, 예외 처리 등 매번 검증
- **기술 부채 누적 방지**: 매 PR마다 개선하므로 몰아서 리팩토링 불필요
- **문서화 자동화**: PR 리뷰 코멘트가 설계 결정 기록 역할
- **학습 가속화**: 같은 실수 반복 시 즉시 지적

### 비용 효율 분석

**절감 비용 (추정):**

| 항목                           | 이전 비용            | 현재 비용           | 절감액        |
| ------------------------------ | -------------------- | ------------------- | ------------- |
| 프로덕션 버그 대응             | 주 8시간 (월 32시간) | 주 1시간 (월 4시간) | **월 28시간** |
| 코드 리뷰 외주                 | 월 $300              | $0                  | **$300**      |
| 긴급 핫픽스 비용               | 월 2-3회 × 4시간     | 0회                 | **월 10시간** |
| 학습 비용 (온라인 강의/멘토링) | 월 $100              | $0                  | **$100**      |

**개발 시간 → 비용 환산 (시급 $50 기준):**

- 월 38시간 절감 = **월 $1,900 절감**

**투자 비용:**

```
GPT-4o API: 월 $45-55 (PR 개수에 따라)
서버 운영: $0 (기존 EC2 활용)
개발 시간: 1주 (초기), 월 1시간 (유지보수)
총 투자: 월 $50 + 유지보수 $50 = $100
```

**ROI 계산:**

```
절감액: $1,900 (시간 가치) + $400 (실제 비용) = $2,300
투자액: $100
ROI = ($2,300 - $100) / $100 = 약 22배
```

물론 이건 "시간을 돈으로 환산"한 추정치지만, **1인 개발자의 시간이 가장 귀한 자원**이라는 점에서 의미가 있습니다.

### 1인 개발자로서의 성장

**기술적 성장:**

8주간 리뷰를 받으며 자연스럽게 체득한 것들:

- **트랜잭션 경계**: 언제 분리해야 하는지 감이 생김
- **동시성 패턴**: Pessimistic Lock, Redis Atomic 연산 이해
- **AOP 활용**: 횡단 관심사를 어떻게 분리하는지
- **이벤트 기반 설계**: 결합도를 낮추는 방법
- **예외 처리 전략**: CustomException 계층 구조

온라인 강의로 배우는 것과 **실제 내 코드에 적용하며 배우는 것**의 차이를 느꼈습니다.

**설계 능력 향상:**

```
Before: "일단 동작하게 만들자"
  ↓
After: "이렇게 설계하면 나중에 확장하기 쉽다"
```

- 책임 분리가 자연스럽게 됨
- 계층 구조를 의식하게 됨
- "나중에 고치기 쉬운" 코드를 짜게 됨

**심리적 효과:**

**불안감 → 확신:**

- Before: "이게 맞나... 배포해도 되나..."
- After: "이 부분은 리뷰에서 OK 받았으니 괜찮아"

**고립감 해소:**

- AI지만 "대화하는 느낌"이 있음
- 매 PR마다 피드백을 받는다는 심리적 안정감
- 혼자서도 "함께 개발하는" 느낌

**지속 가능한 성장:**

- 이전: "성장하고 있나?" 불안
- 현재: "계속 배우고 있다" 확신
- 8주 전 코드 vs 현재 코드를 비교하면 확연한 차이

### 프로젝트 성공 가능성 증가

**품질 향상 → 유저 신뢰:**

- 프로덕션 버그 83% 감소
- 서비스 안정성 향상
- 긴급 점검 0회

**빠른 개발 → 기능 출시 속도:**

- 리팩토링 시간 단축
- 버그 수정 사이클 단축
- 새 기능 개발에 집중 가능

**1인 개발 지속 가능성:**

- 번아웃 위험 감소
- 기술 부채 통제 가능
- 혼자서도 계속 성장 가능

이 시스템 없이 계속했다면, 아마 3-4개월 후에는 **기술 부채에 묻혀** 새 기능 개발보다 버그 수정에 시간을 더 쓰고 있었을 겁니다.

---

## 🚀 향후 개선 계획

현재 시스템은 MVP 수준입니다. 앞으로 추가할 개선 사항들:

### 1. 비용 최적화

**현재 비용**: 월 $45-55

**최적화 방안:**

1. **파일 필터링**: gradle, xml, md 같은 설정/문서 파일 제외 → 20-30% 절감 예상
2. **Diff 압축**: 불필요한 context 라인 제거 → 40-50% 절감 예상
3. **캐싱**: 동일 diff 재리뷰 방지 → 10-15% 절감 예상

**목표**: 월 $30-35로 비용 절감

### 2. 리뷰 품질 향상

- **Fine-tuning**: 내 프로젝트 리뷰 데이터로 모델 학습
- **RAG 도입**: 프로젝트 문서, 과거 리뷰 참조
- **Context 확장**: 전체 PR 맥락을 이해하는 리뷰

### 3. 기능 확장

- **PR 수정 시 증분 리뷰**: synchronize 이벤트도 처리
- **특정 파일 우선 리뷰**: 중요한 파일(Service, Repository)에 집중
- **Slack 알림**: 중요한 지적이 있을 때 알림
- **재시도 로직**: API 실패 시 자동 재시도로 성공률 향상

### 4. 분석 및 모니터링

- **리뷰 이력 통계**: 어떤 문제가 자주 발생하는지
- **성장 지표**: 시간에 따른 코드 품질 변화 추적
- **대시보드**: 리뷰 현황 시각화

---

## 💭 마치며: 혼자서도 성장할 수 있다

### 숫자로 보는 8주간의 변화

이 시스템을 만들고 운영하며 얻은 구체적인 성과를 정리하면:

```
📊 운영 지표
├─ 총 PR: 87개
├─ 자동 리뷰 성공률: 97.7%
├─ 평균 리뷰 시간: 2분 43초
└─ 월 운영 비용: $45-55

🐛 사전 발견 이슈
├─ 동시성 버그: 7건
├─ N+1 성능 문제: 12건
├─ 트랜잭션 범위 문제: 23건
├─ 예외 처리 누락: 18건
└─ 보안 취약점: 3건
총 60+ 건의 실무 이슈 사전 차단

📈 프로덕션 품질 개선
├─ 버그 발생률: 83% ↓
├─ 긴급 핫픽스: 100% ↓ (0회)
├─ 리팩토링 결정 시간: 87% ↓ (2시간 → 15분)
└─ 버그 수정 사이클: 75% ↓ (2일 → 당일)

💰 비용 대비 효과
├─ 투자 비용: 월 $100
├─ 절감 비용: 월 $2,300 (추정)
└─ ROI: 약 22배
```

**가장 중요한 한 가지:**

> **"혼자서도 성장하고 있다는 확신"**

숫자로 증명할 수 없지만, 매일 코드를 짜면서 느끼는 이 확신이 가장 큰 수확입니다.

### 이 프로젝트를 통해 배운 것

**1. 제약은 창의성을 낳는다**

"코드 리뷰를 받을 수 없다"는 제약이, "AI 리뷰 시스템을 만든다"는 해결책으로 이어졌습니다. 1인 개발이라는 한계가 오히려 성장의 기회가 됐습니다.

**2. AI는 도구다**

AI가 사람을 대체하는 게 아닙니다. **올바르게 사용하면 강력한 학습 도구**가 됩니다. 중요한 건 "어떻게 사용하느냐"입니다.

**3. 완벽할 필요는 없다**

이 시스템도 완벽하지 않습니다. 가끔 쓸데없는 지적도 하고, 놓치는 것도 있습니다. 하지만 **없는 것보다 훨씬 낫습니다**.

### 비슷한 고민을 하는 당신에게

혼자 개발하면서 "내 코드가 맞는 걸까?" 고민하고 계신가요?

**지금 바로 시작하세요.** 완벽한 시스템을 만들려 하지 마세요.

1. 가장 간단한 버전부터 (Flask + GPT + Webhook)
2. 사용하면서 개선 (프롬프트가 제일 중요)
3. 비용 최적화는 나중에
4. 꾸준히 활용

이 글의 전체 코드는 제 GitHub에 공개돼 있습니다. 참고하시고, 여러분만의 버전을 만들어보세요.

**혼자서도, 충분히 성장할 수 있습니다.** 🚀

---

## 📚 참고 자료

- 프로젝트 코드: `/server/codereview-llm/`
- [GitHub Webhooks 문서](https://docs.github.com/en/webhooks)
- [OpenAI API 문서](https://platform.openai.com/docs)

---

**작성**: 2025년 10월  
**프로젝트**: Dataracy  
**기간**: 8주 (87 PRs 리뷰)  
**스택**: Flask, GPT-4o, GitHub API, Docker  
**비용**: 월 $45-55

_"제약은 한계가 아니라 창의성의 시작점이다"_
