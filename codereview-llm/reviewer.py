import requests
from prompt_file_review import build_file_review_prompt
from prompt_file_refactor import build_file_refactor_prompt
from diff_parser import extract_changed_files
from diff_struct_parser import parse_diff_structure
from utils import call_gpt

def get_inline_and_refactor_comments(diff_text: str) -> list[dict]:
    """파일 기반 인라인 + 리팩토링 코멘트를 생성하여 리스트로 반환"""
    comments = []

    parsed_files = extract_changed_files(diff_text)
    file_structs = parse_diff_structure(diff_text)

    for file in parsed_files:
        path = file['path']
        file_diff = file['content']
        file_structure = file_structs.get(path)

        # 🔹 1. 코드 블럭 기반 인라인 코멘트들
        inline_prompt = build_file_review_prompt(file_diff)
        inline_response = call_gpt(inline_prompt)
        inline_blocks = inline_response.strip().split("\n\n")

        for block in inline_blocks:
            block = block.strip()
            if not block:
                continue
            comments.append({
                "path": path,
                "body": f"💬 GPT 리뷰 코멘트 (내용 기반)\n\n{block}"
            })

        # 🔹 2. 구조적 리팩토링 코멘트 (1개만 추가)
        refactor_prompt = build_file_refactor_prompt(file_diff)
        refactor_response = call_gpt(refactor_prompt)
        refactor_response = refactor_response.strip()

        if refactor_response:
            comments.append({
                "path": path,
                "body": f"🛠️ 리팩토링 제안\n\n{refactor_response}"
            })

    return comments
