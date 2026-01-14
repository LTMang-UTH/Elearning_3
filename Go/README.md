# WebSocket Chat – Golang

Ứng dụng chat realtime sử dụng WebSocket được xây dựng bằng Golang.

## Chức năng
- Chat realtime nhiều người
- Đặt tên người dùng
- Hiển thị số người online
- Thông báo tham gia / rời phòng chat

## Công nghệ
- Go (Golang)
- WebSocket
- Thư viện gorilla/websocket
- HTML + JavaScript

## Cách chạy
```bash
go mod init websocket-demo
go get github.com/gorilla/websocket
go run main.go
