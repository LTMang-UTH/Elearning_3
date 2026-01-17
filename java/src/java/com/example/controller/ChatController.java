package com.example.controller;

import com.example.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller xử lý các messages từ chat
 * 
 * WebSocket Handshake Process:
 * 1. Client gửi HTTP request với Upgrade header
 * 2. Server phản hồi với 101 Switching Protocols
 * 3. Connection được nâng cấp từ HTTP sang WebSocket
 * 4. Cả hai phía có thể gửi/nhận frames
 */
@Controller
public class ChatController {

    /**
     * Xử lý tin nhắn chat từ client
     * 
     * @MessageMapping("/chat.sendMessage"): 
     *   - Client gửi message đến /app/chat.sendMessage
     *   - Server xử lý và broadcast đến /topic/public
     * 
     * @SendTo("/topic/public"):
     *   - Tất cả clients subscribe /topic/public sẽ nhận được message này
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Thêm timestamp
        chatMessage.setTimestamp(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        return chatMessage;
    }

    /**
     * Xử lý khi user tham gia chat
     * Lưu username vào session và thông báo cho các users khác
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Lưu username vào WebSocket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + " đã tham gia chat!");
        chatMessage.setTimestamp(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        
        return chatMessage;
    }
}

