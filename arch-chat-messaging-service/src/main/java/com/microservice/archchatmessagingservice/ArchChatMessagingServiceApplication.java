package com.microservice.archchatmessagingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ArchChatMessagingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchChatMessagingServiceApplication.class, args);
    }

}
