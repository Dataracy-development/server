from flask import Flask, request
import threading
import requests
import os
from dotenv import load_dotenv
from reviewer import review

load_dotenv()

app = Flask(__name__)

# 환경 변수
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
REPO_NAME = os.getenv("REPO_NAME")

# GitHub 댓글 달기 함수
def post_comment(pr_number, body):
    url = f"https://api.github.com/repos/{REPO_NAME}/issues/{pr_number}/comments"
    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github+json"
    }
    response = requests.post(url, headers=headers, json={"body": body})
    if response.status_code != 201:
        print(f"❌ 댓글 작성 실패: {response.status_code} - {response.text}")
    else:
        print("✅ PR 리뷰 댓글 등록 완료")

# 비동기로 실행할 리뷰 처리 함수
def handle_review(pr_number, diff_url):
    diff_response = requests.get(diff_url)
    if diff_response.status_code != 200:
        print(f"❌ Diff 요청 실패: {diff_response.status_code}")
        return
    diff = diff_response.text
    review_result = review(diff)
    post_comment(pr_number, review_result)

# Webhook 엔드포인트
@app.route("/webhook", methods=["POST"])
def webhook():
    payload = request.json
    if payload.get("action") not in ["opened", "reopened"]:
        return "Ignored", 200

    pr_number = payload["pull_request"]["number"]
    diff_url = payload["pull_request"]["diff_url"]

    # 스레드로 review 실행
    threading.Thread(target=handle_review, args=(pr_number, diff_url)).start()

    # 즉시 응답 반환 (GitHub 타임아웃 방지)
    return "✅ Review started", 200

# 개발 환경 실행
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
