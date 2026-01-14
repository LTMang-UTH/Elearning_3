using System.Net.WebSockets;
using System.Text;
using System.Collections.Concurrent;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

// 1️⃣ Bật WebSocket
app.UseWebSockets();

// 2️⃣ Cho phép dùng file HTML
app.UseDefaultFiles();
app.UseStaticFiles();

// 3️⃣ Lưu danh sách client
var clients = new ConcurrentDictionary<WebSocket, string>();

// 4️⃣ Endpoint WebSocket
app.Map("/ws", async context =>
{
    if (!context.WebSockets.IsWebSocketRequest)
    {
        context.Response.StatusCode = 400;
        return;
    }

    var socket = await context.WebSockets.AcceptWebSocketAsync();
    var buffer = new byte[1024];

    // 👉 Nhận username đầu tiên
    var result = await socket.ReceiveAsync(buffer, CancellationToken.None);
    var username = Encoding.UTF8.GetString(buffer, 0, result.Count);

    clients.TryAdd(socket, username);

    await Broadcast($"🔔 {username} joined the chat");
    await SendOnlineCount();

    try
    {
        while (socket.State == WebSocketState.Open)
        {
            result = await socket.ReceiveAsync(buffer, CancellationToken.None);

            if (result.MessageType == WebSocketMessageType.Close)
                break;

            var message = Encoding.UTF8.GetString(buffer, 0, result.Count);
            await Broadcast($"{username}: {message}");
        }
    }
    finally
    {
        clients.TryRemove(socket, out _);
        await Broadcast($"❌ {username} left the chat");
        await SendOnlineCount();
        await socket.CloseAsync(WebSocketCloseStatus.NormalClosure, "Closed", CancellationToken.None);
    }
});

// 5️⃣ Gửi message cho tất cả client
async Task Broadcast(string message)
{
    var data = Encoding.UTF8.GetBytes(message);

    foreach (var client in clients.Keys)
    {
        if (client.State == WebSocketState.Open)
        {
            await client.SendAsync(
                data,
                WebSocketMessageType.Text,
                true,
                CancellationToken.None
            );
        }
    }
}

// 6️⃣ Gửi số người online
async Task SendOnlineCount()
{
    await Broadcast($"👥 Online users: {clients.Count}");
}

app.Run();
