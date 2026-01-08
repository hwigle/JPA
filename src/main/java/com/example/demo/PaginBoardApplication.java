package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PaginBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaginBoardApplication.class, args);
	}

}
