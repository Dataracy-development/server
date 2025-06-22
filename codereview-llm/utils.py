import os
import openai
import json

openai.api_key = os.getenv("OPENAI_API_KEY")

def call_gpt(prompt: str, model: str = "gpt-4o") -> str:
    response = openai.ChatCompletion.create(
        model=model,
        messages=[{"role": "user", "content": prompt}],
        temperature=0.3,
    )
    return response.choices[0].message["content"]

def save_failed_prompt(prompt: str, error_message: str = ""):
    with open("retry_queue.json", "a") as f:
        json.dump({"prompt": prompt, "error": error_message}, f, ensure_ascii=False)
        f.write("\n")