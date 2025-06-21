# diff_parser.py

def parse_diff_by_file(diff_text: str) -> dict:
    """
    GitHub PR에서 받은 전체 diff 내용을 파일별로 나누는 함수입니다.

    반환 구조:
    {
        "src/main/java/com/example/FooService.java": "diff --git ...\n@@ ...",
        "src/main/resources/application.yml": "diff --git ...\n@@ ...",
        ...
    }
    """
    files = {}
    current_file = None
    buffer = []

    for line in diff_text.splitlines():
        if line.startswith("diff --git"):
            if current_file:
                files[current_file] = "\n".join(buffer)
                buffer.clear()
            # ex) diff --git a/file b/file → file
            current_file = line.split(" b/")[-1].strip()
        elif current_file:
            buffer.append(line)

    if current_file:
        files[current_file] = "\n".join(buffer)

    return files
