package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartInventoryApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Smart Inventory Management System Started  ");
        System.out.println("  Developed by: Hari Krishna                 ");
        System.out.println("  API Base URL: http://localhost:8080/api    ");
        System.out.println("==============================================");
    }
}
