package com.hkdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.hkdev.backend.persistence.repositories")
public class HkdevApplication {

	public static void main(String[] args) {
		SpringApplication.run(HkdevApplication.class, args);
	}
}
