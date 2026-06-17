package com.contact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ConatactBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConatactBookApplication.class, args);
	}

	@PostConstruct
	public void test() {
	    System.out.println("MAIL_USERNAME = "
	            + System.getenv("MAIL_USERNAME"));

	    System.out.println("MAIL_PASSWORD EXISTS = "
	            + (System.getenv("MAIL_PASSWORD") != null));
	}
}
