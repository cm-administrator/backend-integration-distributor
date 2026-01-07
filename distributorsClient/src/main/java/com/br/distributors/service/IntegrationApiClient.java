package com.br.distributors.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class IntegrationApiClient {

	private final WebClient webClient;

	public IntegrationApiClient(WebClient.Builder webClientBuilder, @Value("${integracao.api.base-url}") String baseUrl,
			@Value("${integracao.api.token:}") String token) {
		WebClient.Builder b = webClientBuilder.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		if (token != null && !token.isBlank()) {
			b.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.trim());
		}

		this.webClient = b.build();
	}

	public ApiResult postCustomers(String json) {
		return postJson("/customers/import", json);
	}

	public ApiResult postProducts(String json) {
		return postJson("/products/import", json);
	}

	public ApiResult postSales(String json) {
		return postJson("/sales/import", json);
	}

	public ApiResult postSalesPersons(String json) {
		return postJson("/salespersons/import", json);
	}

	public ApiResult postStock(String json) {
		return postJson("/stocks/import", json);
	}

	private ApiResult postJson(String path, String json) {
		try {
			return webClient.post().uri(path).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.bodyValue(
							json)
					.exchangeToMono(resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
							.map(body -> new ApiResult(resp.statusCode().is2xxSuccessful(), resp.statusCode().value(),
									body)))
					.timeout(Duration.ofSeconds(30)).block();
		} catch (WebClientResponseException e) {
			return ApiResult.error(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (Exception e) {
			return ApiResult.error(-1, e.getMessage());
		}
	}

	public record ApiResult(boolean success, int status, String body) {
		static ApiResult error(int status, String body) {
			return new ApiResult(false, status, body);
		}
	}
}
