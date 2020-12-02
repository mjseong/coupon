package com.assignment.coupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "taskExpireExecutor")
    public Executor taskExpiredPool(){
        ThreadPoolTaskExecutor taskExpireExector = new ThreadPoolTaskExecutor();
        taskExpireExector.setCorePoolSize(1); // pool의 idle 값
        taskExpireExector.setMaxPoolSize(1);// pool의 max값
        taskExpireExector.setQueueCapacity(5); // thread pool max시 thread가 대기하는 큐
        taskExpireExector.setThreadNamePrefix("Task-executor-expire");
        taskExpireExector.initialize();
        return taskExpireExector;
    }

    @Bean(name = "taskSendMessageExecutor")
    public Executor taskSendMessagePool(){
        ThreadPoolTaskExecutor taskExpireExector = new ThreadPoolTaskExecutor();
        taskExpireExector.setCorePoolSize(1); // pool의 idle 값
        taskExpireExector.setMaxPoolSize(1);// pool의 max값
        taskExpireExector.setQueueCapacity(5); // thread pool max시 thread가 대기하는 큐
        taskExpireExector.setThreadNamePrefix("Task-executor-sendMessage");
        taskExpireExector.initialize();
        return taskExpireExector;
    }
}
