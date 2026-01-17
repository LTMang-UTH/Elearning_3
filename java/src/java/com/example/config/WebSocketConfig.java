package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Cấu hình WebSocket với STOMP protocol
 * STOMP (Simple Text Oriented Messaging Protocol) là một giao thức messaging
 * chạy trên WebSocket, giúp đơn giản hóa việc gửi/nhận messages
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Cấu hình message broker
     * - /topic: dùng cho broadcast messages (một server gửi đến nhiều clients)
     * - /queue: dùng cho point-to-point messages (một server gửi đến một client cụ thể)
     * - /app: prefix cho các messages từ client đến server
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker cho /topic và /queue
        config.enableSimpleBroker("/topic", "/queue");
        // Set prefix cho messages từ client đến server
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Đăng ký STOMP endpoint
     * Client sẽ kết nối đến endpoint này để thiết lập WebSocket connection
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint /ws
        // withSockJS() cho phép fallback nếu browser không hỗ trợ WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép tất cả origins (trong production nên giới hạn)
                .withSockJS();
        
        // Endpoint không dùng SockJS (native WebSocket)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}

