from flask import Flask, request
import threading
import requests
import os
from dotenv import load_dotenv
from reviewer import get_summary_comment, get_inline_comments

load_dotenv()

app = Flask(__name__)

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
REPO_NAME = os.getenv("REPO_NAME")

headers = {
    "Authorization": f"Bearer {GITHUB_TOKEN}",
    "Accept": "application/vnd.github+json"
}

# ✅ 기존 요약 댓글 찾기
def find_existing_summary_comment(pr_number):
    url = f"https://api.github.com/repos/{REPO_NAME}/issues/{pr_number}/comments"
    response = requests.get(url, headers=headers)
    if response.status_code != 200:
        print("❌ 요약 댓글 목록 조회 실패")
        return None

    comments = response.json()
    for comment in comments:
        if comment["user"]["type"] == "Bot" and "## 📦 PR 전체 요약" in comment["body"]:
            return comment["id"]
    return None

# ✅ 댓글 수정
def update_comment(comment_id, body):
    url = f"https://api.github.com/repos/{REPO_NAME}/issues/comments/{comment_id}"
    response = requests.patch(url, headers=headers, json={"body": body})
    if response.status_code != 200:
        print(f"❌ 댓글 수정 실패: {response.status_code} - {response.text}")

# ✅ 새 댓글 생성
def create_comment(pr_number, body):
    url = f"https://api.github.com/repos/{REPO_NAME}/issues/{pr_number}/comments"
    response = requests.post(url, headers=headers, json={"body": body})
    if response.status_code != 201:
        print(f"❌ 댓글 생성 실패: {response.status_code} - {response.text}")

# ✅ 리뷰 생성 + 줄 단위 코멘트 통합
def create_review_with_comments(pr_number, comments):
    url = f"https://api.github.com/repos/{REPO_NAME}/pulls/{pr_number}/reviews"

    review_body = "🔍 아래 줄별 리뷰 코멘트를 확인해주세요."
    review_comments = []

    for c in comments:
        review_comments.append({
            "path": c["path"],
            "line": c["line"],
            "side": "RIGHT",
            "body": c["comment"]
        })

    payload = {
        "body": review_body,
        "event": "COMMENT",  # 리뷰 승인/요청이 아닌 일반 코멘트
        "comments": review_comments
    }

    response = requests.post(url, headers=headers, json=payload)
    if response.status_code != 200:
        print(f"❌ 리뷰 생성 실패: {response.status_code} - {response.text}")

# 🧠 리뷰 처리 로직
def handle_review(pr_number, diff_url):
    diff_response = requests.get(diff_url)
    if diff_response.status_code != 200:
        print("❌ Diff 요청 실패")
        return

    diff_text = diff_response.text

    # 1️⃣ 전체 요약 댓글
    summary_body = get_summary_comment(diff_text)
    comment_id = find_existing_summary_comment(pr_number)
    if comment_id:
        update_comment(comment_id, summary_body)
    else:
        create_comment(pr_number, summary_body)

    # 2️⃣ 줄 리뷰 → 하나의 review로 통합
    inline_comments = get_inline_comments(diff_text)
    if inline_comments:
        create_review_with_comments(pr_number, inline_comments)

# 🎯 GitHub Webhook 엔드포인트
@app.route("/webhook", methods=["POST"])
def webhook():
    payload = request.json
    if payload.get("action") not in ["opened", "reopened", "synchronize"]:
        return "Ignored", 200

    pr_number = payload["pull_request"]["number"]
    diff_url = payload["pull_request"]["diff_url"]

    threading.Thread(target=handle_review, args=(pr_number, diff_url)).start()
    return "✅ Review triggered", 200

# 로컬 실행
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
