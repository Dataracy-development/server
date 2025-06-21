# webhook.py

from flask import Flask, request
from reviewer import review
import requests
import os
from dotenv import load_dotenv

# ✅ .env 파일에서 환경변수 로딩
load_dotenv()

# ✅ 환경 변수
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
REPO_NAME = os.getenv("REPO_NAME")

# ✅ Flask 앱
app = Flask(__name__)

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

@app.route("/webhook", methods=["POST"])
def webhook():
    payload = request.json
    if payload.get("action") not in ["opened", "reopened"]:  # 🔧 수정
        return "Ignored", 200

    # PR 번호 및 diff URL 추출
    pr_number = payload["pull_request"]["number"]
    diff_url = payload["pull_request"]["diff_url"]

    # PR diff 가져오기
    diff_response = requests.get(diff_url)
    if diff_response.status_code != 200:
        return f"❌ Diff 요청 실패: {diff_response.status_code}", 500

    diff = diff_response.text

    # 리뷰 수행
    review_result = review(diff)

    # 리뷰 댓글로 등록
    post_comment(pr_number, review_result)

    return "✅ Review Posted", 200

# 개발 환경에서 실행할 때만 실행
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
