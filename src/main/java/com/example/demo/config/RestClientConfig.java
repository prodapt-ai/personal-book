package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Value("${google.books.base-url:https://www.googleapis.com/books/v1}")
	private String baseUrl;
	
	@Bean
	public RestClient restClient() {
		return RestClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader("Accept", "application/json")
				.build();
	}
        
    
}
