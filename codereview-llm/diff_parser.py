import re

def parse_diff_by_file(diff_text: str) -> dict:
    """
    파일별로 diff와 줄 위치(position) 정보를 반환
    """
    files = {}
    current_file = None
    buffer = []
    current_position = {}

    for line in diff_text.splitlines():
        if line.startswith("diff --git"):
            if current_file:
                files[current_file] = {
                    "diff": "\n".join(buffer),
                    "position": current_position
                }
                buffer.clear()
                current_position = {}
            current_file = line.split(" b/")[-1].strip()
        elif line.startswith("@@"):
            try:
                hunk_header = line
                match = re.search(r"\+(\d+)", hunk_header)
                if match:
                    current_line = int(match.group(1))
                    current_position[hunk_header] = current_line
            except:
                pass
            buffer.append(line)
        elif current_file:
            buffer.append(line)

    if current_file:
        files[current_file] = {
            "diff": "\n".join(buffer),
            "position": current_position
        }

    return files
