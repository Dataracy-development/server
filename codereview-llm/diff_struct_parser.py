import re
from typing import List, Dict

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
        approx_line = None
        for line in lines:
            if line["type"] == "context":
                old_to_new[line["old"]] = line["new"]
                approx_line = (line["old"], line["new"])
            elif line["type"] == "add" and approx_line:
                # 추가된 줄도 가장 가까운 context 기준으로 추정 보정
                old_to_new[approx_line[0] + 1] = approx_line[1] + 1
        line_map_by_file[file] = old_to_new
    return line_map_by_file

