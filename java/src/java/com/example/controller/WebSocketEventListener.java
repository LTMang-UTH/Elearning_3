package com.example.controller;

import com.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.model.ChatMessage;

/**
 * Event listener để xử lý các sự kiện WebSocket
 * - SessionConnectedEvent: Khi client kết nối
 * - SessionDisconnectEvent: Khi client ngắt kết nối
 */
@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private DashboardService dashboardService;

    /**
     * Xử lý khi client kết nối
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
        dashboardService.incrementActiveUsers();
    }

    /**
     * Xử lý khi client ngắt kết nối
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        
        if (username != null) {
            System.out.println("User Disconnected: " + username);
            
            // Tạo message thông báo user rời khỏi
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setContent(username + " đã rời khỏi chat!");
            
            // Broadcast message
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
        
        dashboardService.decrementActiveUsers();
    }
}

