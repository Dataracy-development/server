import os
import json
import time
from openai import OpenAI
import tiktoken
from openai import RateLimitError
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def num_tokens_from_string(string: str, model: str = "gpt-4o") -> int:
    encoding = tiktoken.encoding_for_model(model)
    return len(encoding.encode(string))

def split_prompt(prompt: str, max_tokens: int = 8000) -> list[str]:
    lines = prompt.splitlines()
    chunks = []
    current = ""

    for line in lines:
        if num_tokens_from_string(current + line) > max_tokens:
            chunks.append(current.strip())
            current = line + "\n"
        else:
            current += line + "\n"

    if current:
        chunks.append(current.strip())
    return chunks

def call_gpt(prompt, max_retries=3, delay=3):
    for attempt in range(max_retries):
        try:
            response = client.chat.completions.create(
                model="gpt-4o",
                messages=[{"role": "user", "content": prompt}]
            )
            return response.choices[0].message.content
        except RateLimitError as e:
            print(f"🛑 Rate limit hit: {e}. Retrying in {delay}s...")
            time.sleep(delay)
        except Exception as e:
            print(f"❌ Unexpected error: {e}")
            raise e
    raise RuntimeError("Rate limit persisted after retries.")

def save_failed_prompt(prompt: str, error_message: str = ""):
    with open("retry_queue.json", "a") as f:
        json.dump({"prompt": prompt, "error": error_message}, f, ensure_ascii=False)
        f.write("\n")