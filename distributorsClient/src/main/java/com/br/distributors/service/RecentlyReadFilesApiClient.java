package com.br.distributors.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

	public RecentlyReadFilesDto getByDistributorIdentifier(String distributorIdentifier) {
		try {
			return webClient.get().uri(
					uriBuilder -> uriBuilder.path("/recently-read-files/{identifier}").build(distributorIdentifier))
					.retrieve().bodyToMono(RecentlyReadFilesDto.class).block();
		} catch (WebClientResponseException e) {
			if (e.getStatusCode().value() == 404) {
				return null;
			}
			throw e;
		}
	}

	public boolean updateCustomersIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		return postUpdate(distributorIdentifier, "customers", fileName, fileInstant);
	}

	public boolean updateSalesIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		return postUpdate(distributorIdentifier, "sales", fileName, fileInstant);
	}

	public boolean updateStockIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		return postUpdate(distributorIdentifier, "stock", fileName, fileInstant);
	}

	public boolean updateProductsIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		return postUpdate(distributorIdentifier, "products", fileName, fileInstant);
	}

	public boolean updateSalesPersonIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		return postUpdate(distributorIdentifier, "sales-person", fileName, fileInstant);
	}

	private boolean postUpdate(String distributorIdentifier, String endpoint, String fileName, Instant fileInstant) {
		UpdateFileRequest req = new UpdateFileRequest(fileName, fileInstant);

		UpdateResult resp = webClient.post()
				.uri(uriBuilder -> uriBuilder.path("/recently-read-files/{identifier}/{endpoint}")
						.build(distributorIdentifier, endpoint))
				.bodyValue(req).retrieve().bodyToMono(UpdateResult.class).block();

		return resp != null && Boolean.TRUE.equals(resp.updated());
	}

	public static record UpdateFileRequest(String fileName, Instant fileInstant) {
	}

	public static record UpdateResult(Boolean updated) {
	}

	/**
	 * DTO espelho do retorno do GET /recently-read-files/{identifier} Ajuste os
	 * campos conforme o JSON que seu controller devolve.
	 */
	public static class RecentlyReadFilesDto {
		private Long id;

		private String customersFile;
		private Instant customersInstant;

		private String salesFile;
		private Instant salesInstant;

		private String stockFile;
		private Instant stockInstant;

		private String productFile;
		private Instant productInstant;

		private String salesPersonFile;
		private Instant salesPersonInstant;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCustomersFile() {
			return customersFile;
		}

		public void setCustomersFile(String customersFile) {
			this.customersFile = customersFile;
		}

		public Instant getCustomersInstant() {
			return customersInstant;
		}

		public void setCustomersInstant(Instant customersInstant) {
			this.customersInstant = customersInstant;
		}

		public String getSalesFile() {
			return salesFile;
		}

		public void setSalesFile(String salesFile) {
			this.salesFile = salesFile;
		}

		public Instant getSalesInstant() {
			return salesInstant;
		}

		public void setSalesInstant(Instant salesInstant) {
			this.salesInstant = salesInstant;
		}

		public String getStockFile() {
			return stockFile;
		}

		public void setStockFile(String stockFile) {
			this.stockFile = stockFile;
		}

		public Instant getStockInstant() {
			return stockInstant;
		}

		public void setStockInstant(Instant stockInstant) {
			this.stockInstant = stockInstant;
		}

		public String getProductFile() {
			return productFile;
		}

		public void setProductFile(String productFile) {
			this.productFile = productFile;
		}

		public Instant getProductInstant() {
			return productInstant;
		}

		public void setProductInstant(Instant productInstant) {
			this.productInstant = productInstant;
		}

		public String getSalesPersonFile() {
			return salesPersonFile;
		}

		public void setSalesPersonFile(String salesPersonFile) {
			this.salesPersonFile = salesPersonFile;
		}

		public Instant getSalesPersonInstant() {
			return salesPersonInstant;
		}

		public void setSalesPersonInstant(Instant salesPersonInstant) {
			this.salesPersonInstant = salesPersonInstant;
		}
	}
}
