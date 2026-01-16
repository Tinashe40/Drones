package com.tinashe.dronescore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync; // Import for EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableMethodSecurity
@EnableAsync // Enable asynchronous method execution
public class DronesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DronesBackendApplication.class, args);
    }

}
