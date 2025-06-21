import json
import time
from reviewer import ask_gpt
from utils import save_failed_prompt

def process_retry_queue():
    try:
        with open("retry_queue.json", "r") as f:
            lines = f.readlines()
        with open("retry_queue.json", "w") as f:
            f.write("")  # 초기화
        for line in lines:
            entry = json.loads(line)
            print("⏳ GPT 재시도 중...")
            output = ask_gpt(entry["prompt"])
            if output.startswith("[GPT 호출 실패]"):
                save_failed_prompt(entry["prompt"], output)
            else:
                print("✅ 재시도 성공:\n", output[:100])
            time.sleep(2)
    except Exception as e:
        print("❌ 재시도 중 오류:", e)

if __name__ == "__main__":
    process_retry_queue()
