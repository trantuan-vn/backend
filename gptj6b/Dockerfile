# Sử dụng image CUDA chính thức từ NVIDIA
FROM nvidia/cuda:12.6.0-cudnn-devel-ubuntu20.04

# Đặt thư mục làm việc
WORKDIR /app

# Cài đặt các thư viện cần thiết
COPY requirements.txt requirements.txt
RUN apt-get update && \
    apt-get install -y \
    python3-pip \
    python3-dev \
    && pip3 install --upgrade pip && pip3 install -r requirements.txt

# Sao chép mã nguồn vào container
COPY . .

# Chạy ứng dụng
CMD ["python3", "app.py"]

# Mở cổng 5000 cho ứng dụng Flask
EXPOSE 5000
