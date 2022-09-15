package com.egar.employments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"ru.egartech.*", "com.egar.employments"})
@SpringBootApplication
public class EmploymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmploymentsApplication.class, args);
    }

}
