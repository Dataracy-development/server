import os
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

def verify_signature(payload, signature):
    secret = os.getenv("GITHUB_SECRET").encode()
    mac = hmac.new(secret, msg=payload, digestmod=hashlib.sha256)
    expected = 'sha256=' + mac.hexdigest()
    return hmac.compare_digest(expected, signature)

@app.route("/webhook", methods=["POST"])
def webhook():
    # ── 1. 보안 검증
    payload = request.get_data()
    signature = request.headers.get("X-Hub-Signature-256")
    if not verify_signature(payload, signature):
        abort(400, "Invalid signature")

    event = request.headers.get("X-GitHub-Event")
    if event != "pull_request":
        return "Ignored", 200

    data = request.json
    action = data["action"]
    pr = data["pull_request"]
    pr_number = pr["number"]
    diff_url = pr["diff_url"]

    if action not in ["opened", "synchronize", "reopened"]:
        return "Ignored", 200

    # ── 2. Diff 조회
    diff_text = requests.get(diff_url).text

    # ── 3. 전체 요약 (Conversation 탭에 새로 추가)
    summary_prompt = build_summary_prompt(diff_text)
    summary_response = call_gpt(summary_prompt).strip()

    requests.post(
        f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments",
        headers={
            "Authorization": f"Bearer {GITHUB_TOKEN}",
            "Accept": "application/vnd.github+json",
        },
        json={
            "body": f"🤖 **GPT PR 전체 요약**\n\n{summary_response}"
        }
    )

    # ── 4. 파일 리뷰 코멘트들 (Conversation 탭, 줄 번호 없이 body)
    review_comments = generate_review_comments(diff_text)
    for comment in review_comments:
        requests.post(
            f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments",
            headers={...},
            json={
                "body": comment["body"]
            }
        )
    return "Review posted", 200


# ✅컨테이너가 계속 실행되도록 하기 위해선 이 부분이 꼭 필요하다
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
