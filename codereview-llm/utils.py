import re
from typing import Optional

def extract_line_number(comment: str, position: dict) -> Optional[int]:
    """
    GPT 코멘트에서 줄 번호를 추출하거나 position 정보를 기반으로 예측합니다.

    1. GPT가 `line N:` 또는 `(N줄)` 같은 패턴을 사용했을 경우 → 해당 줄 사용
    2. 아니면 가장 마지막 줄 (position 기준) 반환
    """
    # 예: "line 42:", "Line 17 -"
    match = re.search(r"[Ll]ine\s*(\d+)", comment)
    if match:
        return int(match.group(1))

    # 예: "(42줄)", "42줄에서 문제가 발생합니다."
    match_kor = re.search(r"(\d+)줄", comment)
    if match_kor:
        return int(match_kor.group(1))

    # fallback: 마지막 변경 위치 (GitHub가 받아주는 경우)
    if position and isinstance(position, dict):
        return max(position.values(), default=1)

    return None
