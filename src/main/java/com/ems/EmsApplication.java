package com.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Employee Management System — Main Application Entry Point
 *
 * @author EMS Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class EmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmsApplication.class, args);
    }
}
