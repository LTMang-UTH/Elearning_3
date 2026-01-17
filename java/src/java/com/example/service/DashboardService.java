package com.example.service;

import com.example.model.DashboardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service gửi dữ liệu real-time đến dashboard
 * Sử dụng @Scheduled để gửi dữ liệu định kỳ
 */
@Service
public class DashboardService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Random random = new Random();
    private final AtomicInteger activeUsers = new AtomicInteger(0);
    private double stockPrice = 100.0;
    private double temperature = 25.0;
    private double humidity = 60.0;
    private double pressure = 1013.25;
    private double cpuUsage = 45.0;
    private double memoryUsage = 65.0;
    private double networkSpeed = 50.0;

    /**
     * Gửi dữ liệu dashboard mỗi 2 giây
     * 
     * WebSocket Frames:
     * - Text Frame: Dữ liệu text (JSON trong trường hợp này)
     * - Binary Frame: Dữ liệu binary
     * - Ping/Pong Frame: Để giữ kết nối alive
     * - Close Frame: Đóng kết nối
     */
    @Scheduled(fixedRate = 2000) // 2 giây
    public void sendDashboardData() {
        // Simulate thay đổi giá cổ phiếu (random walk)
        stockPrice += (random.nextDouble() - 0.5) * 2.0;
        stockPrice = Math.max(50.0, Math.min(150.0, stockPrice)); // Giới hạn trong khoảng 50-150

        // Simulate thay đổi nhiệt độ
        temperature += (random.nextDouble() - 0.5) * 1.0;
        temperature = Math.max(20.0, Math.min(30.0, temperature)); // Giới hạn 20-30 độ C

        // Simulate thay đổi độ ẩm
        humidity += (random.nextDouble() - 0.5) * 2.0;
        humidity = Math.max(40.0, Math.min(80.0, humidity)); // Giới hạn 40-80%

        // Simulate thay đổi áp suất
        pressure += (random.nextDouble() - 0.5) * 5.0;
        pressure = Math.max(990.0, Math.min(1030.0, pressure)); // Giới hạn 990-1030 hPa

        // Simulate CPU usage
        cpuUsage += (random.nextDouble() - 0.5) * 10.0;
        cpuUsage = Math.max(20.0, Math.min(90.0, cpuUsage)); // Giới hạn 20-90%

        // Simulate Memory usage
        memoryUsage += (random.nextDouble() - 0.5) * 8.0;
        memoryUsage = Math.max(30.0, Math.min(85.0, memoryUsage)); // Giới hạn 30-85%

        // Simulate Network speed (Mbps)
        networkSpeed += (random.nextDouble() - 0.5) * 15.0;
        networkSpeed = Math.max(10.0, Math.min(100.0, networkSpeed)); // Giới hạn 10-100 Mbps

        // Lấy số lượng users thực tế đang online (không simulate)
        // Số lượng này chỉ thay đổi khi có user thực sự kết nối/ngắt kết nối
        int users = activeUsers.get();

        // Tạo đối tượng dữ liệu với tất cả metrics
        DashboardData data = new DashboardData(
            Math.round(stockPrice * 100.0) / 100.0,
            Math.round(temperature * 10.0) / 10.0,
            Math.round(humidity * 10.0) / 10.0,
            Math.round(pressure * 100.0) / 100.0,
            Math.round(cpuUsage * 10.0) / 10.0,
            Math.round(memoryUsage * 10.0) / 10.0,
            Math.round(networkSpeed * 10.0) / 10.0,
            users
        );

        // Gửi đến tất cả clients subscribe /topic/dashboard
        messagingTemplate.convertAndSend("/topic/dashboard", data);
    }

    /**
     * Tăng số lượng active users (gọi khi có user mới kết nối)
     */
    public void incrementActiveUsers() {
        activeUsers.incrementAndGet();
    }

    /**
     * Giảm số lượng active users (gọi khi user ngắt kết nối)
     */
    public void decrementActiveUsers() {
        activeUsers.decrementAndGet();
    }
}

