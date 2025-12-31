import asyncio
import json
import random
import websockets
import threading
import webbrowser
from http.server import SimpleHTTPRequestHandler, HTTPServer

# Tap cac client dang ket noi
connected_clients = {}

# ===============================
# Ham xu ly khi co client ket noi
# ===============================
async def handle_client(websocket):
    print("Client moi ket noi")
    username = None
    connected_clients[websocket] = {"username": None}

    try:
        async for message in websocket:
            print(f"Nhan tu client: {message}")
            try:
                msg = json.loads(message)
            except Exception as e:
                await websocket.send(json.dumps({
                    "type": "error",
                    "message": f"Lỗi dữ liệu gửi lên: {str(e)}"
                }))
                continue

            if msg.get("type") == "setUsername":
                username = msg.get("username", "Anonymous")
                connected_clients[websocket]["username"] = username
                await broadcast(json.dumps({
                    "type": "system",
                    "message": f"{username} đã tham gia phòng chat!"
                }))
                await broadcast(json.dumps({
                    "type": "online_count",
                    "count": len(connected_clients)
                }))
            elif msg.get("type") == "chat":
                text = msg.get("text", "")
                from datetime import datetime
                now = datetime.now().strftime("%H:%M:%S")
                await broadcast(json.dumps({
                    "type": "chat",
                    "username": username or "Anonymous",
                    "text": text,
                    "time": now
                }))
            elif msg.get("type") == "ping":
                await websocket.send(json.dumps({"type": "pong"}))

    except websockets.exceptions.ConnectionClosed as e:
        print(f"Client da ngat ket noi: {str(e)}")
        left_name = connected_clients.get(websocket, {}).get("username", "Anonymous")
        await broadcast(json.dumps({
            "type": "system",
            "message": f"{left_name} đã rời phòng chat!"
        }))
        await broadcast(json.dumps({
            "type": "online_count",
            "count": len(connected_clients)
        }))
    except Exception as e:
        print(f"Lỗi server: {str(e)}")
        try:
            await websocket.send(json.dumps({
                "type": "error",
                "message": f"Lỗi server: {str(e)}"
            }))
        except:
            pass
    finally:
        del connected_clients[websocket]


# ===============================
# Ham gui tin cho tat ca client
# ===============================
async def broadcast(message):
    if connected_clients:
        await asyncio.gather(
            *[client.send(message) for client in connected_clients],
            return_exceptions=True
        )


# ==========================================
# Dashboard du lieu real-time (gia lap)
# Minh hoa bat dong bo
# ==========================================
async def send_fake_data():
    while True:
        fake_data = {
            "type": "dashboard",
            "temperature": round(random.uniform(20, 35), 2),
            "stock_price": round(random.uniform(1000, 1500), 2)
        }

        await broadcast(json.dumps(fake_data))

        # Sleep bat dong bo (khong chan server)
        await asyncio.sleep(2)


# ===============================
# Main server
# ===============================
async def main():
    # Khởi động HTTP server phục vụ file tĩnh
    def start_http_server():
        httpd = HTTPServer(("0.0.0.0", 8080), SimpleHTTPRequestHandler)
        print("HTTP Server dang chay tai http://localhost:8080")
        httpd.serve_forever()

    http_thread = threading.Thread(target=start_http_server, daemon=True)
    http_thread.start()

    # Khởi động WebSocket server trên cổng 8765
    ws_server = await websockets.serve(
        handle_client,
        "0.0.0.0",
        8765,
        subprotocols=["chat-protocol"]
    )
    print("WebSocket Server dang chay tai ws://localhost:8765")

    print("Chat Server đang chạy tại: http://localhost:8080/client.html \n")



    # Chạy song song WebSocket server và dashboard data
    await asyncio.gather(
        ws_server.wait_closed(),
        send_fake_data()
    )


if __name__ == "__main__":
    asyncio.run(main())
