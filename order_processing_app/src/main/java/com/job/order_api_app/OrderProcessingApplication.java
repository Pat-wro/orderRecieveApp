package com.job.order_api_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderProcessingApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderProcessingApplication.class, args);
    }
}