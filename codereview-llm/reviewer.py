from dotenv import load_dotenv
load_dotenv()

import os
import openai

from prompt_summary import build_summary_prompt
from prompt_inline import build_inline_prompt
from prompt_refactor import build_refactor_prompt
from diff_parser import parse_diff_by_file

# 환경 변수 불러오기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
REPO_NAME = os.getenv("REPO_NAME")

client = openai.OpenAI(api_key=OPENAI_API_KEY)

# GPT 응답에 의미 있는 내용이 있는지 판단하는 필터 함수
def should_skip_output(output: str) -> bool:
    skip_keywords = [
        "리팩토링 제안 없음",
        "변경 사항 없음",
        "관련 항목 없음",
        "유닛 테스트와 관련이 없습니다",
        "AOP로 분리할 로직은 없습니다",
        "예외 처리가 필요 없습니다",
        "로그와 관련된 사항은 없습니다",
        "테스트와 관련이 없습니다"
    ]
    return any(keyword in output for keyword in skip_keywords)

def ask_gpt(prompt: str) -> str:
    try:
        messages = [
            {"role": "system", "content": "당신은 소프트웨어 아키텍트이자 리뷰어입니다."},
            {"role": "user", "content": prompt}
        ]
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=messages
        )
        return response.choices[0].message.content
    except Exception as e:
        return f"[GPT 호출 실패] {str(e)}"


# 리뷰 대상 확장자 정의
REVIEWABLE_EXTENSIONS = [".java", ".kt", ".py", ".ts", ".js", ".go", ".rb", ".html", ".css"]
SKIP_EXTENSIONS = [".png", ".jpg", ".jpeg", ".gif", ".zip", ".jar", ".pdf", ".exe"]

def review(diff: str) -> str:
    result = []

    # 1. 요약 섹션
    result.append("## 📌 Pull Request 리뷰 요약\n")
    summary_output = ask_gpt(build_summary_prompt(diff))
    result.append(summary_output)

    # 2. 파일별 상세 리뷰
    files = parse_diff_by_file(diff)
    for path, content in files.items():

        # 확장자 필터링
        if not any(path.endswith(ext) for ext in REVIEWABLE_EXTENSIONS):
            continue
        if any(path.endswith(ext) for ext in SKIP_EXTENSIONS):
            continue

        inline_output = ask_gpt(build_inline_prompt(content))
        refactor_output = ask_gpt(build_refactor_prompt(content))

        # 둘 다 건질 내용 없으면 생략
        if should_skip_output(inline_output) and should_skip_output(refactor_output):
            continue

        result.append(f"\n---\n### 🔍 파일: `{path}`\n")

        if not should_skip_output(inline_output):
            result.append(inline_output)

        if not should_skip_output(refactor_output):
            result.append("\n#### 🔧 리팩토링 제안\n")
            result.append(refactor_output)

    return "\n\n".join(result)