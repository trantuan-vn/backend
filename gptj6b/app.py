import torch
import torch.distributed as dist
import torch.multiprocessing as mp
from transformers import GPTJForCausalLM, GPT2Tokenizer
from flask import Flask, request, jsonify

app = Flask(__name__)
# Khai báo biến toàn cục
model = None
tokenizer = None

def train(rank, world_size):
    # Khởi tạo môi trường phân phối
    dist.init_process_group("nccl", rank=rank, world_size=world_size)
    # Tải mô hình và tokenizer
    model_name = "EleutherAI/gpt-j-6B"
    tokenizer = GPT2Tokenizer.from_pretrained(model_name)
    model = GPTJForCausalLM.from_pretrained(model_name)
    # Đặt mô hình vào chế độ phân phối
    model = torch.nn.parallel.DistributedDataParallel(model.to(rank), device_ids=[rank])

def main():
    world_size = 4  # Số lượng GPU
    mp.spawn(train, args=(world_size,), nprocs=world_size, join=True)
    app.run(host='0.0.0.0', port=8080)

@app.route('/generate', methods=['POST'])
def generate_text():
    data = request.json
    input_text = data.get('text', '')
    # Chuyển đổi văn bản đầu vào thành tensor
    inputs = tokenizer(input_text, return_tensors="pt").to('cuda')
    # Sinh văn bản
    outputs = model.generate(**inputs, max_length=50)
    generated_text = tokenizer.decode(outputs[0], skip_special_tokens=True)
    return jsonify({'generated_text': generated_text})

if __name__ == '__main__':
    main()
    
