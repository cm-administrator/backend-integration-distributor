package com.br.distributors.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder().filter(authorizationHeaderFilter()); // Adiciona o filtro de cabeçalho
	}

	private ExchangeFilterFunction authorizationHeaderFilter() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {

			return Mono.just(clientRequest);
		});
	}
}
