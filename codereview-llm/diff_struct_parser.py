import re
from typing import List, Dict

def parse_diff_structure(diff_text: str) -> dict[str, str]:
    """
    전체 diff 텍스트를 파일 단위로 나눠 dict로 반환

    예시:
    {
        "src/service/UserService.java": "diff --git a/src/service/UserService.java b/src/service/UserService.java\n@@ ...",
        ...
    }
    """
    file_blocks = {}
    pattern = re.compile(r"^diff --git a/(.+?) b/.*?\n(.*?)^diff --git", re.MULTILINE | re.DOTALL)
    matches = pattern.findall(diff_text + "\ndiff --git")  # 마지막 파일까지 매칭되도록 끝에 추가

    for file_path, block in matches:
        file_blocks[file_path] = f"diff --git a/{file_path} b/{file_path}\n" + block.strip()

    return file_blocks

def parse_structured_diff(diff_text: str) -> Dict[str, List[Dict]]:
    results = {}
    current_file = None
    old_line = new_line = None

    for line in diff_text.splitlines():
        if line.startswith("diff --git"):
            current_file = line.split(" b/")[-1].strip()
            results[current_file] = []
        elif line.startswith("@@"):
            match = re.match(r"@@ -(\\d+),?\\d* \\+(\\d+),?\\d* @@", line)
            if match:
                old_line = int(match.group(1))
                new_line = int(match.group(2))
            continue
        elif current_file:
            if line.startswith('+') and not line.startswith('+++'):
                results[current_file].append({"old": None, "new": new_line, "type": "add", "line": line})
                new_line += 1
            elif line.startswith('-') and not line.startswith('---'):
                results[current_file].append({"old": old_line, "new": None, "type": "delete", "line": line})
                old_line += 1
            else:
                results[current_file].append({"old": old_line, "new": new_line, "type": "context", "line": line})
                old_line += 1
                new_line += 1
    return results

def extract_line_map(structured_diff: Dict[str, List[Dict]]) -> Dict[str, Dict[int, int]]:
    line_map_by_file = {}
    for file, lines in structured_diff.items():
        old_to_new = {}
        content_map = {}

        for line in lines:
            if line["type"] == "context":
                old_to_new[line["old"]] = line["new"]
                content_map[line["line"].lstrip(" ")] = (line["old"], line["new"])
            elif line["type"] == "add":
                added_content = line["line"].lstrip("+").strip()
                if added_content in content_map:
                    # 같은 줄 내용이 이전 context에 있었으면 매핑
                    old_to_new[content_map[added_content][0]] = line["new"]
                else:
                    # fallback: 가장 가까운 context 기준 보정
                    prev_context = max((o for o in old_to_new if o is not None), default=1)
                    old_to_new[prev_context + 1] = line["new"]

        line_map_by_file[file] = old_to_new
    return line_map_by_file


