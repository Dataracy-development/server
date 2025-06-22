from prompt_file_review import build_file_review_prompt
from diff_parser import extract_changed_files
from utils import call_gpt
import re

def generate_review_comments(diff_text: str) -> list[dict]:
    """파일 기반 리뷰 코멘트를 생성하여 리스트로 반환 (줄 위치 사용 안 함)"""
    comments = []
    parsed_files = extract_changed_files(diff_text)

    for file in parsed_files:
        path = file["path"]
        file_diff = file["content"]

        prompt = build_file_review_prompt(file_diff)
        gpt_response = call_gpt(prompt).strip()

        # ✂️ 여러 코멘트 블럭으로 분리 (🤖 기준으로)
        review_blocks = re.split(r"\n?🤖", gpt_response)
        for block in review_blocks:
            block = block.strip()
            if not block:
                continue
            comments.append({
                "path": path,
                "body": f"🤖 GPT Review Bot:\n\n💬 {block}"
            })

    return comments
