package com.market.ecommerce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	// Fail fast if critical secrets/configuration are missing or insecure
	@Bean
	public ApplicationRunner validateConfig(@Value("${jwt.secret:}") String jwtSecret) {
		return args -> {
			if (jwtSecret == null || jwtSecret.isBlank()) {
				throw new IllegalStateException("JWT secret (JWT_SECRET) must be set as environment variable and must not be empty");
			}
			if (jwtSecret.startsWith("CHANGE_THIS") || jwtSecret.length() < 32) {
				throw new IllegalStateException("JWT secret appears to be insecure; set a sufficiently long random JWT_SECRET (32+ chars)");
			}
		};
	}

}
