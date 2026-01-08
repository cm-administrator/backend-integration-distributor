package com.br.distributors.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.br.distributors.request.BatchUpdateRequest;
import com.br.distributors.response.BatchUpdateResponse;
import com.br.distributors.response.RecentlyReadFilesResponse;

@Service
public class RecentlyReadFilesApiClient {

	private final WebClient webClient;

	public RecentlyReadFilesApiClient(WebClient.Builder webClientBuilder,
			@Value("${integracao.api.base-url}") String baseUrl,
			@Value("${recentlyReadFiles.api.token:}") String token) {

		WebClient.Builder b = webClientBuilder.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		if (token != null && !token.isBlank()) {
			b.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.trim());
		}
		this.webClient = b.build();
	}

	public RecentlyReadFilesResponse getByDistributorIdentifier(String distributorIdentifier) {
		try {
			return webClient.get()
					.uri(uriBuilder -> uriBuilder.path("/recently-read-files/{identifier}")
							.build(distributorIdentifier))
					.retrieve().bodyToMono(RecentlyReadFilesResponse.class).block();
		} catch (WebClientResponseException e) {
			if (e.getStatusCode().value() == 404)
				return null;
			throw e;
		}
	}

	public BatchUpdateResponse updateIfNewerBatch(String distributorIdentifier, BatchUpdateRequest req) {
		return webClient.post()
				.uri(uriBuilder -> uriBuilder.path("/recently-read-files/{identifier}/batch-if-newer")
						.build(distributorIdentifier))
				.bodyValue(req).retrieve().bodyToMono(BatchUpdateResponse.class).block();
	}

}
