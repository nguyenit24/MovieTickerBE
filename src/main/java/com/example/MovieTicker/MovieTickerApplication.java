package com.example.MovieTicker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.MovieTicker.config")
public class MovieTickerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieTickerApplication.class, args);
	}

}
