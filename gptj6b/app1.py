from transformers import GPTJForCausalLM, GPT2Tokenizer

# Tải tokenizer và mô hình GPT-J-6B
tokenizer = GPT2Tokenizer.from_pretrained("EleutherAI/gpt-j-6B")
model = GPTJForCausalLM.from_pretrained("EleutherAI/gpt-j-6B")

# Tạo một dictionary để lưu trữ ngữ cảnh, câu hỏi và trả lời
qa_storage = {}

# Hàm lưu trữ câu hỏi và trả lời
def store_qa(ck_code, question, answer):
    if ck_code not in qa_storage:
        qa_storage[ck_code] = []
    qa_storage[ck_code].append({'question': question, 'answer': answer})

# Hàm sinh văn bản dựa trên ngữ cảnh và lưu trữ câu hỏi và trả lời
def generate_text_with_history(ck_code, question, max_length=100):
    # Kiểm tra nếu mã CK có trong ngữ cảnh
    if ck_code not in qa_storage:
        return "Mã CK không hợp lệ hoặc ngữ cảnh chưa được thiết lập."

    # Lấy lịch sử câu hỏi và trả lời
    history = qa_storage[ck_code]
    history_text = " ".join([f"Q: {item['question']} A: {item['answer']}" for item in history])
    
    # Kết hợp lịch sử với câu hỏi mới
    full_prompt = f"{history_text} Q: {question} A:"

    inputs = tokenizer(full_prompt, return_tensors="pt")
    outputs = model.generate(inputs["input_ids"], max_length=max_length, num_return_sequences=1)
    answer = tokenizer.decode(outputs[0], skip_special_tokens=True).strip()

    # Lưu trữ câu hỏi và trả lời
    store_qa(ck_code, question, answer)
    
    return answer

# Ví dụ cập nhật ngữ cảnh và sinh văn bản
store_qa("CK001", "What is AI?", "AI stands for Artificial Intelligence.")
store_qa("CK001", "How does GPT work?", "GPT is a language model that generates text based on the input prompt.")

question = "Explain the working of GPT in more detail."
print("Kết quả với CK001:")
print(generate_text_with_history("CK001", question))
