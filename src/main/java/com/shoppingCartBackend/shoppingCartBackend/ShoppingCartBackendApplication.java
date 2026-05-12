package com.shoppingCartBackend.shoppingCartBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
public class ShoppingCartBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartBackendApplication.class, args);
	}

}
