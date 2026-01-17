package com.example.model;

import java.util.Map;

/**
 * Model cho dữ liệu dashboard real-time
 * Chứa nhiều metrics: giá cổ phiếu, nhiệt độ, độ ẩm, áp suất, CPU, Memory, Network
 */
public class DashboardData {
    private double stockPrice;
    private double temperature;
    private double humidity;
    private double pressure;
    private double cpuUsage;
    private double memoryUsage;
    private double networkSpeed;
    private int activeUsers;
    private Map<String, Object> metrics;
    private long timestamp;

    public DashboardData() {
        this.timestamp = System.currentTimeMillis();
    }

    public DashboardData(double stockPrice, double temperature, int activeUsers) {
        this.stockPrice = stockPrice;
        this.temperature = temperature;
        this.activeUsers = activeUsers;
        this.timestamp = System.currentTimeMillis();
    }

    public DashboardData(double stockPrice, double temperature, double humidity, 
                       double pressure, double cpuUsage, double memoryUsage, 
                       double networkSpeed, int activeUsers) {
        this.stockPrice = stockPrice;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.networkSpeed = networkSpeed;
        this.activeUsers = activeUsers;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getNetworkSpeed() {
        return networkSpeed;
    }

    public void setNetworkSpeed(double networkSpeed) {
        this.networkSpeed = networkSpeed;
    }
}

