package com.sportygroup.sporty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportyServiceApplication.class, args);
	}

}
