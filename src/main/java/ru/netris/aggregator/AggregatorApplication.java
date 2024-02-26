package ru.netris.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.modelmapper.ModelMapper;

@SpringBootApplication
public class AggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AggregatorApplication.class, args);
	}

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
