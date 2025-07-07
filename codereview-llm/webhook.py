import os
import hmac
import hashlib
import requests
from flask import Flask, request, abort
from reviewer import generate_review_comments
from prompt_summary import build_summary_prompt
from utils import call_gpt, split_prompt, save_failed_prompt
import time

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
    payload = request.get_data()
    signature = request.headers.get("X-Hub-Signature-256")
    if not verify_signature(payload, signature):
        abort(400, "Invalid signature")

    event = request.headers.get("X-GitHub-Event")
    print(f"📩 Received event: {event}")

    if event != "pull_request":
        print("⛔ Not a PR event.")
        return "Ignored", 200

    data = request.json
    action = data.get("action")
    print(f"🪄 PR Action: {action}")

    if action not in ["opened", "synchronize"]:
        print("⛔ Action not in [opened, synchronize]")
        return "Ignored", 200

    # 저장해서 실제 payload 분석
    import json
    with open("payload_debug.json", "w") as f:
        json.dump(data, f, indent=2)

    pr = data.get("pull_request", {})
    pr_number = pr.get("number")
    diff_url = pr.get("diff_url") or pr.get("patch_url") or None
    print(f"🔗 PR #{pr_number} Diff URL: {diff_url}")

    if not diff_url:
        print("❗ diff_url/patch_url not found.")
        return "Ignored", 200

    diff_text = requests.get(diff_url).text

    summary_prompt = build_summary_prompt(diff_text)
    chunks = split_prompt(summary_prompt, max_tokens=8000)

    summary_parts = []
    for idx, chunk in enumerate(chunks, 1):
        try:
            response = call_gpt(chunk).strip()
            summary_parts.append(f"### 📄 파트 {idx}\n{response}")
            time.sleep(3)
        except Exception as e:
            summary_parts.append(f"❌ 파트 {idx} 처리 실패: {str(e)}")
            save_failed_prompt(chunk, str(e))
            break

    summary_body = "\n\n".join(summary_parts)

    requests.post(
        f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments",
        headers={
            "Authorization": f"Bearer {GITHUB_TOKEN}",
            "Accept": "application/vnd.github+json",
        },
        json={
            "body": f"🚀 **GPT PR 전체 요약 (총 {len(chunks)}개 파트)**\n\n{summary_body}"
        }
    )

    review_comments = generate_review_comments(diff_text)
    for comment in review_comments:
        requests.post(
            f"https://api.github.com/repos/{GITHUB_REPO}/issues/{pr_number}/comments",
            headers={
                "Authorization": f"Bearer {GITHUB_TOKEN}",
                "Accept": "application/vnd.github+json",
            },
            json={
                "body": comment["body"]
            }
        )

    return "Review posted", 200



# ✅컨테이너가 계속 실행되도록 하기 위해선 이 부분이 꼭 필요하다
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
