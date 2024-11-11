package com.example.animalproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.animalproductservice.repository")  // Use dots, not slashes
@EntityScan("com.example.animalproductservice.entity")
public class AnimalProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimalProductServiceApplication.class, args);
    }
}

//@SpringBootApplication
//public class com.example.animalproductservice.AnimalProductServiceApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(com.example.animalproductservice.AnimalProductServiceApplication.class, args);
//    }
//}
