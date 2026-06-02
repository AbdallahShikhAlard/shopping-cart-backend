package com.shoppingCartBackend.shoppingCartBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cache.annotation.EnableCaching;

@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
@EnableCaching
public class ShoppingCartBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartBackendApplication.class, args);
	}

}
