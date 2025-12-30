// server.js - WebSocket Chat Server + phục vụ index.html
const http = require("http");
const fs = require("fs");
const path = require("path");
const WebSocket = require("ws");

// Đọc file index.html (phải đặt cùng thư mục với server.js)
const html = fs.readFileSync(path.join(__dirname, "index.html"), "utf8");

// Tạo HTTP server để phục vụ trang chat
const server = http.createServer((req, res) => {
  res.writeHead(200, { "Content-Type": "text/html" });
  res.end(html);
});

// Tạo WebSocket server
const wss = new WebSocket.Server({ server });

// Lưu client kèm theo tên người dùng
const clients = new Map(); // ws → { username }

wss.on("connection", (ws) => {
  // Mặc định tên tạm thời
  clients.set(ws, { username: "Đang đặt tên..." });

  // Gửi thông báo có người mới tham gia
  broadcastSystem(`${clients.size} người đang online`);

  ws.on("message", (data) => {
    let msg;
    try {
      msg = JSON.parse(data);
    } catch (e) {
      return; // Bỏ qua nếu không phải JSON
    }

    const clientInfo = clients.get(ws);

    // Xử lý đặt tên
    if (msg.type === "setUsername") {
      const oldName = clientInfo.username;
      clientInfo.username = msg.username || "Anonymous";
      console.log(`[✓] ${oldName} đổi tên thành ${clientInfo.username}`);
      broadcastSystem(`${clientInfo.username} đã tham gia phòng chat!`);
      return;
    }

    // Xử lý tin nhắn chat
    if (msg.type === "chat" && msg.text) {
      const formatted = {
        type: "chat",
        username: clientInfo.username,
        text: msg.text,
        time: new Date().toLocaleTimeString("vi-VN"),
      };
      console.log(`[←] ${formatted.username}: ${formatted.text}`);
      broadcast(formatted);
    }
  });

  ws.on("close", () => {
    const clientInfo = clients.get(ws);
    broadcastSystem(`${clientInfo.username} đã rời phòng chat.`);
    clients.delete(ws);
    broadcastSystem(`${clients.size} người đang online`);
  });
});

// Hàm broadcast tin hệ thống
function broadcastSystem(message) {
  broadcast({ type: "system", message });
}

// Hàm broadcast chung
function broadcast(message) {
  const data = JSON.stringify(message);
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(data);
    }
  });
}

const PORT = 8080;
server.listen(PORT, () => {
  console.log(`🚀 Chat Server đang chạy tại: http://localhost:${PORT}`);
  console.log(`   → Mở trình duyệt và truy cập địa chỉ trên để chat ngay!`);
});
