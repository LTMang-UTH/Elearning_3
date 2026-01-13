# C++ Asynchronous Chat Demo

## Mục tiêu
Mô phỏng kỹ thuật **lập trình bất đồng bộ** trong C++ thông qua mô hình **Client – Server**, nhằm tăng hiệu suất xử lý nhiều kết nối đồng thời.

## Mô tả
- **Server (C++)**:  
  - Chạy socket TCP
  - Xử lý nhiều client đồng thời (non-blocking)
  - Nhận và phản hồi dữ liệu theo cơ chế bất đồng bộ

- **Client (HTML + JavaScript)**:  
  - Chạy trên trình duyệt
  - Gửi yêu cầu HTTP bất đồng bộ
  - Hiển thị kết quả giống giao diện web chat

- **client.cpp** (tùy chọn):  
  - Client C++ đơn giản dùng để kiểm thử server

## Cấu trúc thư mục
