from flask import Flask, request
import threading
import requests
import os
from dotenv import load_dotenv
from reviewer import get_summary_comment, get_inline_comments
from utils import fetch_existing_review_comments, match_existing_comment

import concurrent.futures

executor = concurrent.futures.ThreadPoolExecutor(max_workers=2)

load_dotenv()
app = Flask(__name__)

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
REPO_NAME = os.getenv("REPO_NAME")

headers = {
    "Authorization": f"Bearer {GITHUB_TOKEN}",
    "Accept": "application/vnd.github+json"
}

def create_review_with_inline_tracking(pr_number, new_comments):
    existing_comments = fetch_existing_review_comments(pr_number, GITHUB_TOKEN, REPO_NAME)
    review_comments = []

    for new_c in new_comments:
        match = match_existing_comment(new_c, existing_comments)
        if match and match["body"] != new_c["comment"]:
            patch_url = f"https://api.github.com/repos/{REPO_NAME}/pulls/comments/{match['id']}"
            response = requests.patch(patch_url, headers=headers, json={"body": new_c["comment"]})
            if response.status_code != 200:
                print(f"❌ 코멘트 수정 실패: {response.text}")
        elif not match:
            review_comments.append({
                "path": new_c["path"],
                "line": new_c["line"],
                "side": "RIGHT",
                "body": new_c["comment"]
            })

    if review_comments:
        url = f"https://api.github.com/repos/{REPO_NAME}/pulls/{pr_number}/reviews"
        payload = {
            "body": "🔍 아래 줄별 리뷰 코멘트를 확인해주세요.",
            "event": "COMMENT",
            "comments": review_comments
        }
        res = requests.post(url, headers=headers, json=payload)
        if res.status_code != 200:
            print(f"❌ 줄별 리뷰 생성 실패: {res.text}")

def handle_review(pr_number, diff_url):
    diff_response = requests.get(diff_url)
    if diff_response.status_code != 200:
        print("❌ Diff 요청 실패")
        return

    diff_text = diff_response.text
    summary_body = get_summary_comment(diff_text)

    comment_url = f"https://api.github.com/repos/{REPO_NAME}/issues/{pr_number}/comments"
    res = requests.get(comment_url, headers=headers)
    existing = res.json() if res.status_code == 200 else []
    summary_id = next((c["id"] for c in existing if "📦 PR 전체 요약" in c["body"]), None)

    if summary_id:
        patch_url = f"https://api.github.com/repos/{REPO_NAME}/issues/comments/{summary_id}"
        requests.patch(patch_url, headers=headers, json={"body": summary_body})
    else:
        requests.post(comment_url, headers=headers, json={"body": summary_body})

    inline_comments = get_inline_comments(diff_text)
    if inline_comments:
        create_review_with_inline_tracking(pr_number, inline_comments)

@app.route("/webhook", methods=["POST"])
def webhook():
    payload = request.json
    if payload.get("action") not in ["opened", "reopened", "synchronize"]:
        return "Ignored", 200

    pr_number = payload["pull_request"]["number"]
    diff_url = payload["pull_request"]["diff_url"]

    executor.submit(handle_review, pr_number, diff_url)
    return "✅ Review triggered", 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
