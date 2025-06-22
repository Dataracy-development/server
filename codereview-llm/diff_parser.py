import re

def extract_changed_files(diff_text: str) -> list[dict]:
    """
    전체 PR diff에서 변경된 파일들의 경로와 해당 diff 내용 추출
    """
    pattern = re.compile(r"^diff --git a/(.+?) b/.*?$", re.MULTILINE)
    matches = pattern.finditer(diff_text)

    files = []
    lines = diff_text.splitlines()
    indices = [match.start() for match in matches] + [len(diff_text)]

    for i in range(len(indices) - 1):
        start = indices[i]
        end = indices[i + 1]
        segment = diff_text[start:end]
        path_match = re.search(r"^diff --git a/(.+?) b/", segment)
        if path_match:
            files.append({
                "path": path_match.group(1).strip(),
                "content": segment.strip()
            })

    return files
