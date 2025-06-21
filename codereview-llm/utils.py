import re
import requests
import time
import json
from typing import Optional
import hashlib


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

def fetch_existing_review_comments(pr_number: int, token: str, repo: str) -> list[dict]:
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {
        "Authorization": f"Bearer {token}",
        "Accept": "application/vnd.github.v3+json"
    }
    response = requests.get(url, headers=headers)
    if response.status_code != 200:
        raise Exception(f"[GitHub API 실패] 리뷰 코멘트 조회 실패: {response.text}")
    return response.json()

def save_failed_prompt(prompt: str, error: str):
    entry = {"timestamp": int(time.time()), "prompt": prompt, "error": error}
    try:
        with open("retry_queue.json", "a") as f:
            f.write(json.dumps(entry, ensure_ascii=False) + "\n")
    except Exception as e:
        print(f"[Fallback 저장 실패] {e}")

def hash_comment_body(body: str) -> str:
    return hashlib.sha256(body.encode("utf-8")).hexdigest()

def match_existing_comment(new_comment: dict, existing_comments: list[dict]) -> Optional[dict]:
    new_hash = hash_comment_body(new_comment["comment"])
    for old in existing_comments:
        if (old["path"] == new_comment["path"] and
            old["line"] == new_comment["line"] and
            old["side"] == "RIGHT"):
            old_hash = hash_comment_body(old["body"])
            if old_hash == new_hash:
                return old  # 내용까지 동일
    return None
