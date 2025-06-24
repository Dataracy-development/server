import os
import json
from openai import OpenAI

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def call_gpt(prompt):
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content

def save_failed_prompt(prompt: str, error_message: str = ""):
    with open("retry_queue.json", "a") as f:
        json.dump({"prompt": prompt, "error": error_message}, f, ensure_ascii=False)
        f.write("\n")