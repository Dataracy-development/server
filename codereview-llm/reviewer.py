import os
import openai
from dotenv import load_dotenv
from prompt_inline import build_inline_prompt
from prompt_summary import build_summary_prompt
from diff_parser import parse_diff_by_file
from diff_struct_parser import parse_structured_diff, extract_line_map
from utils import extract_line_number, save_failed_prompt

load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = openai.OpenAI(api_key=OPENAI_API_KEY)

REVIEWABLE_EXTENSIONS = [".java", ".kt", ".py", ".ts", ".js"]
SKIP_PHRASES = ["리뷰할 것 없음", "수정할 부분이 없습니다"]

def ask_gpt(prompt: str) -> str:
    try:
        messages = [
            {"role": "system", "content": "당신은 소프트웨어 아키텍트입니다."},
            {"role": "user", "content": prompt}
        ]
        response = client.chat.completions.create(model="gpt-4o", messages=messages)
        return response.choices[0].message.content.strip()
    except Exception as e:
        save_failed_prompt(prompt, str(e))
        return f"[GPT 호출 실패] {str(e)}"

def get_summary_comment(diff: str) -> str:
    output = ask_gpt(build_summary_prompt(diff))
    return f"## 📦 PR 전체 요약\n---\n{output}"

def get_inline_comments(diff: str) -> list[dict]:
    files = parse_diff_by_file(diff)
    structured = parse_structured_diff(diff)
    line_map = extract_line_map(structured)
    inline_comments = []

    for path, meta in files.items():
        if not any(path.endswith(ext) for ext in REVIEWABLE_EXTENSIONS):
            continue
        if path not in structured:
            continue

        file_lines = structured[path]
        position = meta["position"]
        changed_lines = [l["line"] for l in file_lines if l["type"] in ("add", "delete")]
        file_diff = "\n".join(changed_lines).strip()
        if not file_diff:
            continue

        inline_output = ask_gpt(build_inline_prompt(file_diff))
        if any(skip in inline_output for skip in SKIP_PHRASES):
            continue

        for comment in inline_output.split("\n"):
            if not comment.strip():
                continue
            line = extract_line_number(comment, position)
            if line is not None and path in line_map:
                mapped = line_map[path].get(line)
                if mapped:
                    line = mapped
            if line is None:
                continue
            inline_comments.append({
                "path": path,
                "line": line,
                "comment": comment.strip()
            })

    return inline_comments
