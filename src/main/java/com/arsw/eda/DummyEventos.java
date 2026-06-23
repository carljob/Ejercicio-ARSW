package com.arsw.eda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DummyEventos {
    public static void main(String[] args) {
        SpringApplication.run(DummyEventos.class, args);
    }
}
