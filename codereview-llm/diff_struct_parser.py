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
            match = re.match(r"@@ -(\d+),?\d* \+(\d+),?\d* @@", line)
            if match:
                old_line = int(match.group(1))
                new_line = int(match.group(2))
            continue
        elif current_file:
            if line.startswith('+') and not line.startswith('+++'):
                results[current_file].append({
                    "old": None, "new": new_line, "type": "add", "line": line
                })
                new_line += 1
            elif line.startswith('-') and not line.startswith('---'):
                results[current_file].append({
                    "old": old_line, "new": None, "type": "delete", "line": line
                })
                old_line += 1
            else:
                results[current_file].append({
                    "old": old_line, "new": new_line, "type": "context", "line": line
                })
                old_line += 1
                new_line += 1

    return results
