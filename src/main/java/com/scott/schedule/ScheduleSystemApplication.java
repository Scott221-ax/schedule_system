package com.scott.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 排班排课系统启动类
 *
 * @author mazhenpeng
 * @since 2025/8/29
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@MapperScan("com.scott.schedule.mapper")
public class ScheduleSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleSystemApplication.class, args);
    }
}

