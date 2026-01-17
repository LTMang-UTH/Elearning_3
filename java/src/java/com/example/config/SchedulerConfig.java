package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Cấu hình để enable scheduling
 * Cần thiết cho @Scheduled annotation trong DashboardService
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}

