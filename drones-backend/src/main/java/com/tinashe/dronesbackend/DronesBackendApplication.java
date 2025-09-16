package com.tinashe.dronesbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DronesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DronesBackendApplication.class, args);
    }

}
