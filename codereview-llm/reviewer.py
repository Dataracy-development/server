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

def ask_gpt(prompt: str) -> str:
    try:
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "당신은 소프트웨어 아키텍트이자 리뷰어입니다."},
                {"role": "user", "content": prompt}
            ]
        )
        return response.choices[0].message.content
    except Exception as e:
        return f"[GPT 호출 실패] {str(e)}"


def review(diff: str) -> str:
    result = []
    result.append("## 📌 Pull Request 리뷰 요약\n")
    result.append(ask_gpt(build_summary_prompt(diff)))

    files = parse_diff_by_file(diff)
    for path, content in files.items():
        result.append(f"\n---\n### 🔍 파일: `{path}`\n")
        result.append(ask_gpt(build_inline_prompt(content)))
        result.append("\n#### 🔧 리팩토링 제안\n")
        result.append(ask_gpt(build_refactor_prompt(content)))

    return "\n\n".join(result)
