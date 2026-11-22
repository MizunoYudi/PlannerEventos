package com.example.plannereventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlannerEventosApplication {

    public static void main(String[] args) {

        SpringApplication.run(PlannerEventosApplication.class, args);
    }

}
