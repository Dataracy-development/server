import json
import os
import time
from utils import call_gpt, save_failed_prompt

RETRY_FILE = "retry_queue.json"

def process_retry_queue():
    if not os.path.exists(RETRY_FILE):
        print("📂 retry_queue.json 파일이 존재하지 않습니다.")
        return

    try:
        with open(RETRY_FILE, "r") as f:
            lines = f.readlines()

        if not lines:
            print("📭 재시도할 항목이 없습니다.")
            return

        # 파일 초기화
        open(RETRY_FILE, "w").close()

        for i, line in enumerate(lines, 1):
            entry = json.loads(line)
            prompt = entry.get("prompt", "")
            if not prompt:
                continue

            print(f"\n⏳ [{i}] GPT 재시도 중...")
            response = call_gpt(prompt).strip()

            if response.startswith("[GPT 호출 실패]") or not response:
                print("❌ 실패 → 다시 큐에 적재")
                save_failed_prompt(prompt, response)
            else:
                print("✅ 성공 → 일부 출력:")
                print(response[:120] + "..." if len(response) > 120 else response)

            time.sleep(2)

    except Exception as e:
        print("❌ 재시도 중 예외 발생:", e)

if __name__ == "__main__":
    process_retry_queue()
