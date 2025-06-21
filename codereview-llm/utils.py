import re
import requests
import time
import json
from typing import Optional

def extract_line_number(comment: str, position: dict) -> Optional[int]:
    match = re.search(r"[Ll]ine\s*(\d+)", comment)
    if match:
        return int(match.group(1))

    match_kor = re.search(r"(\d+)줄", comment)
    if match_kor:
        return int(match_kor.group(1))

    if position and isinstance(position, dict):
        return max(position.values(), default=1)

    return None

def get_diff_between_commits(repo: str, base: str, head: str, token: str) -> str:
    url = f"https://api.github.com/repos/{repo}/compare/{base}...{head}"
    headers = {
        "Authorization": f"Bearer {token}",
        "Accept": "application/vnd.github.v3.diff"
    }

    response = requests.get(url, headers=headers)
    if response.status_code != 200:
        raise Exception(f"GitHub compare API failed: {response.text}")
    return response.text

def save_failed_prompt(prompt: str, error: str):
    entry = {
        "timestamp": int(time.time()),
        "prompt": prompt,
        "error": error
    }
    try:
        with open("retry_queue.json", "a") as f:
            f.write(json.dumps(entry, ensure_ascii=False) + "\n")
    except Exception as e:
        print(f"[Fallback 저장 실패] {e}")
