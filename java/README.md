# 🚀 Ứng dụng WebSocket Demo - Lập trình Bất đồng bộ

Ứng dụng Java Spring Boot mô phỏng các kỹ thuật lập trình bất đồng bộ sử dụng WebSocket để giao tiếp thời gian thực giữa client và server.

## 📋 Mục Lục

- [Giới thiệu](#giới-thiệu)
- [Kiến thức WebSocket](#kiến-thức-websocket)
- [Cấu trúc Dự án](#cấu-trúc-dự-án)
- [Cài đặt và Chạy](#cài-đặt-và-chạy)
- [Tính năng](#tính-năng)
- [Giải thích Kỹ thuật](#giải-thích-kỹ-thuật)

## 🎯 Giới thiệu

Dự án này bao gồm:

1. **Chat Real-time**: Ứng dụng chat thời gian thực với broadcast messages
2. **Dashboard Real-time**: Dashboard hiển thị dữ liệu được cập nhật tức thời (giá cổ phiếu, nhiệt độ, số lượng users)

## 📚 Kiến thức WebSocket

### WebSocket là gì?

WebSocket là một giao thức truyền thông hai chiều (bidirectional) cho phép client và server giao tiếp thời gian thực qua một kết nối TCP duy nhất. Khác với HTTP request-response truyền thống, WebSocket cho phép server "push" dữ liệu đến client mà không cần client phải request.

### WebSocket Handshake

Quá trình "nâng cấp" kết nối HTTP lên WebSocket:

```
1. Client gửi HTTP Request với headers:
   GET /ws HTTP/1.1
   Host: localhost:8080
   Upgrade: websocket
   Connection: Upgrade
   Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
   Sec-WebSocket-Version: 13

2. Server phản hồi với:
   HTTP/1.1 101 Switching Protocols
   Upgrade: websocket
   Connection: Upgrade
   Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=

3. Kết nối được nâng cấp, cả hai phía có thể gửi/nhận frames
```

### WebSocket Frames

Dữ liệu được gửi qua WebSocket được đóng gói trong các frames:

| Frame Type | Opcode | Mô tả |
|------------|--------|-------|
| **Text Frame** | 0x1 | Dữ liệu text (JSON, XML, plain text) |
| **Binary Frame** | 0x2 | Dữ liệu binary (hình ảnh, file) |
| **Close Frame** | 0x8 | Đóng kết nối một cách "duyên dáng" |
| **Ping Frame** | 0x9 | Kiểm tra kết nối còn sống |
| **Pong Frame** | 0xA | Phản hồi cho Ping |
| **Continuation Frame** | 0x0 | Tiếp tục frame lớn (fragmented) |

### STOMP Protocol

STOMP (Simple Text Oriented Messaging Protocol) là một giao thức messaging chạy trên WebSocket, giúp đơn giản hóa việc gửi/nhận messages:

- **Destinations**: `/topic/public` (broadcast), `/queue/private` (point-to-point)
- **Commands**: `CONNECT`, `SEND`, `SUBSCRIBE`, `UNSUBSCRIBE`, `DISCONNECT`
- **Headers**: `destination`, `content-type`, `content-length`

## 📁 Cấu trúc Dự án

```
websocket-demo/
├── pom.xml
├── README.md
└── src/
    ├── java/
    │   └── com/example/
    │       ├── WebSocketApplication.java              # Main class
    │       ├── config/
    │       │   ├── WebSocketConfig.java              # WebSocket configuration
    │       │   └── SchedulerConfig.java               # Scheduling configuration
    │       ├── controller/
    │       │   ├── ChatController.java                # Chat message handler
    │       │   └── WebSocketEventListener.java         # Connection events
    │       ├── model/
    │       │   ├── ChatMessage.java                  # Chat message model
    │       │   └── DashboardData.java                 # Dashboard data model
    │       └── service/
    │           └── DashboardService.java             # Real-time data service
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html                             # Trang chủ
            ├── chat.html                              # Chat client
            └── dashboard.html                         # Dashboard client
```

## 🛠️ Cài đặt và Chạy

### Yêu cầu

- Java 17 hoặc cao hơn
- Maven 3.6+ (hoặc sử dụng Maven Wrapper - đã được tích hợp sẵn)

### Thiết lập JAVA_HOME (Windows PowerShell)

Nếu chưa có biến môi trường JAVA_HOME, bạn cần set nó trong PowerShell:

```powershell
# Tìm đường dẫn Java
where.exe java

# Set JAVA_HOME (thay đổi đường dẫn phù hợp với máy bạn)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
```

**Lưu ý**: Để set vĩnh viễn, thêm vào System Environment Variables hoặc sử dụng script `build.ps1`.

### Các bước

1. **Clone hoặc tải dự án**

2. **Build dự án**

   **Cách 1: Sử dụng Maven Wrapper (Khuyến nghị - không cần cài Maven)**
   ```powershell
   # Set JAVA_HOME nếu chưa set
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
   
   # Build
   .\mvnw.cmd clean install
   ```
   
   **Cách 2: Sử dụng script helper**
   ```powershell
   .\build.ps1 install
   ```
   
   **Cách 3: Sử dụng Maven (nếu đã cài)**
   ```bash
   mvn clean install
   ```

3. **Chạy ứng dụng**

   **Cách 1: Sử dụng Maven Wrapper**
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
   .\mvnw.cmd spring-boot:run
   ```
   
   **Cách 2: Sử dụng script helper**
   ```powershell
   .\build.ps1 run
   ```
   
   **Cách 3: Chạy JAR trực tiếp**
   ```bash
   java -jar target/websocket-demo-1.0.0.jar
   ```

4. **Truy cập ứng dụng**
   - Mở trình duyệt và truy cập: `http://localhost:8080`
   - Chọn "Chat Real-time" hoặc "Dashboard Real-time"

### Chạy với nhiều clients

Để test broadcast messages, mở nhiều tab trình duyệt cùng lúc:
- Chat: Mở nhiều tab `http://localhost:8080/chat.html`
- Dashboard: Mở nhiều tab `http://localhost:8080/dashboard.html`

## ✨ Tính năng

### 1. Chat Real-time

- ✅ Kết nối WebSocket với STOMP
- ✅ Nhập username và tham gia chat
- ✅ Gửi/nhận tin nhắn thời gian thực
- ✅ Broadcast tin nhắn đến tất cả clients
- ✅ Thông báo khi user tham gia/rời khỏi
- ✅ Hiển thị timestamp cho mỗi tin nhắn
- ✅ UI đẹp với animations

### 2. Dashboard Real-time

- ✅ Cập nhật dữ liệu mỗi 2 giây
- ✅ Hiển thị giá cổ phiếu (simulated)
- ✅ Hiển thị nhiệt độ cảm biến (simulated)
- ✅ Hiển thị số lượng users online
- ✅ Biểu đồ real-time với Chart.js
- ✅ Hiển thị thay đổi (tăng/giảm) với màu sắc

## 🔧 Giải thích Kỹ thuật

### Server-side (Java Spring Boot)

#### 1. WebSocket Configuration (`WebSocketConfig.java`)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    // Cấu hình message broker
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
    
    // Đăng ký endpoint
    registry.addEndpoint("/ws").withSockJS();
}
```

- `/topic/*`: Dùng cho broadcast (một server → nhiều clients)
- `/queue/*`: Dùng cho point-to-point (một server → một client)
- `/app/*`: Prefix cho messages từ client đến server

#### 2. Chat Controller (`ChatController.java`)

```java
@MessageMapping("/chat.sendMessage")
@SendTo("/topic/public")
public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    // Xử lý và broadcast message
}
```

- `@MessageMapping`: Endpoint nhận messages từ client
- `@SendTo`: Broadcast message đến tất cả subscribers

#### 3. Dashboard Service (`DashboardService.java`)

```java
@Scheduled(fixedRate = 2000)
public void sendDashboardData() {
    // Tạo dữ liệu giả
    DashboardData data = new DashboardData(...);
    
    // Gửi đến /topic/dashboard
    messagingTemplate.convertAndSend("/topic/dashboard", data);
}
```

- `@Scheduled`: Tự động chạy định kỳ
- `SimpMessagingTemplate`: Gửi messages đến clients

### Client-side (JavaScript)

#### 1. Kết nối WebSocket

```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, onConnected, onError);
```

#### 2. Subscribe để nhận messages

```javascript
stompClient.subscribe('/topic/public', (payload) => {
    const message = JSON.parse(payload.body);
    displayMessage(message);
});
```

#### 3. Gửi message

```javascript
stompClient.send("/app/chat.sendMessage", {}, 
    JSON.stringify(chatMessage));
```

### WebSocket API (Browser)

| API | Mô tả |
|-----|-------|
| `new WebSocket(url)` | Tạo kết nối WebSocket |
| `websocket.onopen` | Event khi kết nối được thiết lập |
| `websocket.onmessage` | Event khi nhận được message |
| `websocket.onerror` | Event khi có lỗi |
| `websocket.onclose` | Event khi kết nối bị đóng |
| `websocket.send(data)` | Gửi dữ liệu |
| `websocket.close()` | Đóng kết nối |

## 🎓 So sánh với HTTP Polling

| Đặc điểm | HTTP Polling | WebSocket |
|----------|--------------|-----------|
| **Latency** | Cao (phải chờ request) | Thấp (push ngay lập tức) |
| **Overhead** | Cao (HTTP headers mỗi request) | Thấp (chỉ frame headers) |
| **Server Push** | Không (phải polling) | Có (server có thể push) |
| **Bidirectional** | Không (request-response) | Có (cả hai chiều) |
| **Connection** | Tạm thời (mỗi request) | Duy trì (persistent) |

## 🔍 Xử lý Lỗi và Đóng Kết nối

### Graceful Close

```javascript
// Client đóng kết nối
stompClient.disconnect(() => {
    console.log('Disconnected');
});

// Server xử lý disconnect
@EventListener
public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    // Cleanup resources
}
```

### Error Handling

```javascript
stompClient.connect({}, onConnected, (error) => {
    console.error('Connection error:', error);
    // Retry logic
});
```

## 🚀 Mở rộng

### Subprotocols

WebSocket hỗ trợ subprotocols để xác định format dữ liệu:

```java
registry.addEndpoint("/ws")
    .setAllowedOriginPatterns("*")
    .withSockJS()
    .setSubProtocols("chat", "dashboard");
```

### Binary Data

Gửi dữ liệu binary (hình ảnh, file):

```java
@MessageMapping("/upload")
public void handleBinary(byte[] data) {
    // Xử lý binary data
}
```

### Authentication

Thêm authentication cho WebSocket:

```java
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new AuthChannelInterceptor());
}
```

## 📖 Tài liệu Tham khảo

- [RFC 6455 - The WebSocket Protocol](https://tools.ietf.org/html/rfc6455)
- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [MDN WebSocket API](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
- [STOMP Protocol Specification](https://stomp.github.io/)
