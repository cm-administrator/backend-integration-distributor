package com.br.distributors.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Distributor;
import com.br.distributors.models.Product;
import com.br.distributors.models.Stock;
import com.br.distributors.repository.StockRepository;
import com.br.distributors.request.StockFileResponse;
import com.br.distributors.request.StockResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
public class StockService {

	private final StockRepository stockRepository;
	private final ProductService productService;
	private final DistributorService distributorService;

	public StockService(StockRepository stockRepository, ProductService productService,
			DistributorService distributorService) {
		this.stockRepository = stockRepository;
		this.productService = productService;
		this.distributorService = distributorService;
	}

	@Transactional(readOnly = true)
	public Stock findById(Long stockId) {
		return stockRepository.findById(stockId)
				.orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para ID: " + stockId));
	}

	/**
	 * Ajuste o critério de busca conforme seu domínio: - se estoque é por
	 * produto+distribuidor, use os dois campos - se estoque é só por produto, use
	 * apenas barcode
	 */
	@Transactional(readOnly = true)
	private Optional<Stock> findByBarcodeAndDistributorIdentifier(String barcode, String distributorIdentifier) {
		return stockRepository.findByProductBarcodeAndDistributorIdentifier(barcode, distributorIdentifier);
	}

	@Transactional(readOnly = true)
	public Stock getByBarcodeAndDistributorIdentifier(String barcode, String distributorIdentifier) {
		return findByBarcodeAndDistributorIdentifier(barcode, distributorIdentifier)
				.orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para o EAN: " + barcode));
	}

	/**
	 * Insere apenas stocks ainda inexistentes (por barcode do produto). Evita N
	 * queries (uma por item) buscando todos os barcodes existentes de uma vez.
	 *
	 * Observação: se o seu estoque for por (distribuidor + produto), troque o
	 * conjunto de chaves para usar ambos e ajuste o repository.
	 */
	@Transactional
	public void saveAll(StockFileResponse response) {
		List<StockResponse> items = response.getItems();
		if (items == null || items.isEmpty())
			return;

		LocalDate stockDate = response.getStockDate();

		// Coleta barcodes e distribuidores presentes no arquivo (para busca em lote)
		Set<String> barcodes = items.stream().map(StockResponse::getProductBarcode).filter(Objects::nonNull)
				.map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		if (barcodes.isEmpty())
			return;

		Set<String> distributorIdentifiers = items.stream().map(i -> i.distributorAgentIdentifier)
				.filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		// Busca existentes em lote (vai trazer combinações distribuidor+barcode
		// existentes)
		List<Stock> existingStocks = stockRepository
				.findAllByDistributorIdentifierInAndProductBarcodeIn(distributorIdentifiers, barcodes);

		// Mapa por chave composta: distributor|barcode
		Map<String, Stock> existingByKey = existingStocks.stream().collect(Collectors
				.toMap(s -> key(s.getDistributor().getIdentifier(), s.getProduct().getBarcode()), s -> s, (a, b) -> a));

		// Processa: insere novos e atualiza existentes quando a data for diferente
		List<Stock> toInsert = items.stream().map(item -> {
			String barcode = item.getProductBarcode() == null ? "" : item.getProductBarcode().trim();
			String distId = item.distributorAgentIdentifier == null ? "" : item.distributorAgentIdentifier.trim();

			if (barcode.isBlank() || distId.isBlank())
				return null;

			Stock existing = existingByKey.get(key(distId, barcode));

			if (existing == null) {
				return toStock(item, stockDate);
			}

			// regra: se a data do arquivo for diferente da registrada, atualiza quantity
			if (!stockDate.equals(existing.getStockDate())) {
				existing.setQuantity(item.getQuantity()); // ajuste se o getter for outro
				existing.setStockDate(stockDate); // recomendado manter consistente
			}

			return null;
		}).filter(Objects::nonNull).toList();

		if (!toInsert.isEmpty()) {
			stockRepository.saveAll(toInsert);
		}
	}

	private String key(String distributorIdentifier, String barcode) {
		return distributorIdentifier + "|" + barcode;
	}

	private Stock toStock(StockResponse response, LocalDate stockDate) {
		Product product = productService.getByBarcode(response.getProductBarcode());
		if (product == null) {
			return null;
		} else {
			Distributor distributor = distributorService.getByIdentifier(response.distributorAgentIdentifier);
			return new Stock(response, product, distributor, stockDate);
		}

	}
}
