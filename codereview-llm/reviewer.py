from dotenv import load_dotenv
load_dotenv()

import os
import openai
from prompt_summary import build_summary_prompt
from prompt_inline import build_inline_prompt
from diff_parser import parse_diff_by_file
from utils import extract_line_number  # 줄 번호 추출 함수 (직접 구현 필요)

# 환경 변수
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = openai.OpenAI(api_key=OPENAI_API_KEY)

REVIEWABLE_EXTENSIONS = [".java", ".kt", ".py", ".ts", ".js", ".go", ".rb", ".html", ".css"]
SKIP_EXTENSIONS = [".png", ".jpg", ".jpeg", ".gif", ".zip", ".jar", ".pdf", ".exe"]
SKIP_PHRASES = [
    "리팩토링 제안 없음",
    "변경 사항 없음",
    "수정할 부분이 없습니다",
    "괜찮은 코드입니다",
    "크게 문제 없어 보입니다"
]

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
        return response.choices[0].message.content.strip()
    except Exception as e:
        return f"[GPT 호출 실패] {str(e)}"

# ✅ 요약용
def get_summary_comment(diff: str) -> str:
    output = ask_gpt(build_summary_prompt(diff))
    return f"""## 📦 PR 전체 요약
---
{output}
"""

# ✅ 줄 단위 코드 리뷰용
def get_inline_comments(diff: str) -> list[dict]:
    files = parse_diff_by_file(diff)
    inline_comments = []

    for path, meta in files.items():
        if not any(path.endswith(ext) for ext in REVIEWABLE_EXTENSIONS):
            continue
        if any(path.endswith(ext) for ext in SKIP_EXTENSIONS):
            continue

        file_diff = meta["diff"]
        position = meta["position"]

        inline_output = ask_gpt(build_inline_prompt(file_diff))

        if any(skip in inline_output for skip in SKIP_PHRASES):
            continue

        # 줄 번호 기준으로 분할된 코멘트 추출 (한 줄당 하나씩)
        comments = inline_output.split("\n")
        for comment in comments:
            if not comment.strip():
                continue

            line = extract_line_number(comment, position)  # 이 함수는 줄번호를 추출하는 유틸로 따로 구현
            if line is None:
                continue

            inline_comments.append({
                "path": path,
                "line": line,
                "comment": comment.strip()
            })

    return inline_comments
