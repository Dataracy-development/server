import os
import re
import hmac
import hashlib
import requests
from flask import Flask, request, abort
from reviewer import generate_review_comments
from prompt_summary import build_summary_prompt
from utils import call_gpt

app = Flask(__name__)

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
GITHUB_REPO = os.getenv("GITHUB_REPO")
GITHUB_SECRET = os.getenv("GITHUB_SECRET")


def verify_signature(payload: bytes, signature: str) -> bool:
    """GitHub Webhook Signature 검증"""
    mac = hmac.new(GITHUB_SECRET.encode(), msg=payload, digestmod=hashlib.sha256)
    expected = 'sha256=' + mac.hexdigest()
    return hmac.compare_digest(expected, signature)


@app.route("/webhook", methods=["POST"])
def webhook():
    # 🔐 보안 검증
    payload = request.get_data()
    signature = request.headers.get("X-Hub-Signature-256")
    if not verify_signature(payload, signature):
        abort(400, "Invalid signature")

    # 📦 PR 이벤트 여부 확인
    event = request.headers.get("X-GitHub-Event")
    if event != "pull_request":
        return "Ignored", 200

    data = request.json
    action = data.get("action")
    pr = data.get("pull_request", {})
    pr_number = pr.get("number")
    diff_url = pr.get("diff_url")

    if action != "opened":
        return "Ignored", 200

    # 📑 Diff 텍스트 수집
    diff_text = requests.get(diff_url).text

    # ✨ 1. 전체 요약 코멘트
    summary_prompt = build_summary_prompt(diff_text)
    summary_response = call_gpt(summary_prompt).strip()

    post_github_comment(pr_number, f"🚀 **GPT PR 전체 요약**\n\n{summary_response}")

    # ✨ 2. 파일별 리뷰 코멘트들
    review_comments = generate_review_comments(diff_text)
    for comment in review_comments:
        post_github_comment(pr_number, comment["body"])

    return "Review posted", 200


def post_github_comment(pr_number: int, body: str):
    """GitHub PR에 코멘트 등록"""
    requests.post(
        f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments",
        headers={
            "Authorization": f"Bearer {GITHUB_TOKEN}",
            "Accept": "application/vnd.github+json",
        },
        json={"body": body},
    )


# 컨테이너 실행 지속
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)