# reviewer.py
import openai
import os
from prompt_summary import build_summary_prompt
from prompt_inline import build_inline_prompt
from prompt_refactor import build_refactor_prompt
from diff_parser import parse_diff_by_file

openai.api_key = os.getenv("OPENAI_API_KEY")

def ask_gpt(prompt: str) -> str:
    response = openai.ChatCompletion.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": prompt}],
        temperature=0.2
    )
    return response.choices[0].message.content.strip()

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
