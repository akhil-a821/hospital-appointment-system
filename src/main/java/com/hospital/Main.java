package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application Entry Point.
 * Launches embedded web server on port 8080.
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("==================================================================");
        System.out.println("  CarePulse Hospital Appointment Scheduling System Started!       ");
        System.out.println("  Desktop URL:  http://localhost:8080                             ");
        System.out.println("  Mobile Access: http://<your-local-ip>:8080                      ");
        System.out.println("==================================================================");
    }
}
