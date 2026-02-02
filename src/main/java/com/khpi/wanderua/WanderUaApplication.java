package com.khpi.wanderua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.khpi.wanderua")
public class WanderUaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WanderUaApplication.class, args);
    }
}
