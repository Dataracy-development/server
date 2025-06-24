package com.dataracy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
		scanBasePackages = {
		"com.dataracy",
		"cores.user_module.config"
	}
)
public class DataracyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataracyApplication.class, args);
	}

}
