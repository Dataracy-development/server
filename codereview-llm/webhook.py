# webhook.py
from flask import Flask, request
from reviewer import review
import requests, os

app = Flask(__name__)

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
REPO_NAME = os.getenv("REPO_NAME")

def post_comment(pr_number, body):
    url = f"https://api.github.com/repos/{REPO_NAME}/issues/{pr_number}/comments"
    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github+json"
    }
    requests.post(url, headers=headers, json={"body": body})

@app.route("/webhook", methods=["POST"])
def webhook():
    payload = request.json
    if payload.get("action") != "opened":
        return "Ignored", 200

    pr_number = payload["pull_request"]["number"]
    diff_url = payload["pull_request"]["diff_url"]
    diff = requests.get(diff_url).text

    review_result = review(diff)
    post_comment(pr_number, review_result)
    return "Review Posted", 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
