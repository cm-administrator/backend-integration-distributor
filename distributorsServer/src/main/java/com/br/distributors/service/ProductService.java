package com.br.distributors.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Product;
import com.br.distributors.models.Supplier;
import com.br.distributors.repository.ProductRepository;
import com.br.distributors.request.ProductFileResponse;
import com.br.distributors.request.ProductResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final SupplierService supplierService;

	public ProductService(ProductRepository productRepository, SupplierService supplierService) {
		this.productRepository = productRepository;
		this.supplierService = supplierService;
	}

	@Transactional(readOnly = true)
	public Product findById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado para ID: " + productId));
	}

	@Transactional(readOnly = true)
	private Optional<Product> findByBarcode(String barcode) {
		return productRepository.findByBarcode(barcode);
	}

	@Transactional(readOnly = true)
	public Product getByBarcode(String barcode) {
		return findByBarcode(barcode).orElse(null);
	}

	/**
	 * Insere apenas produtos ainda inexistentes (por SKU). Evita N queries (uma por
	 * item) buscando todos os SKUs existentes de uma vez.
	 */
	@Transactional
	public void saveAll(ProductFileResponse file) {
		List<ProductResponse> items = file.getProducts();

		if (items.isEmpty())
			return;

		Set<String> barcodes = items.stream().map(ProductResponse::getBarcode).filter(Objects::nonNull)
				.map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		if (barcodes.isEmpty())
			return;

		Set<String> existing = productRepository.findExistingBarcodes(barcodes);

		List<Product> toInsert = items.stream().filter(r -> r.getBarcode() != null && !r.getBarcode().isBlank())
				.filter(r -> !existing.contains(r.getBarcode().trim())).map(this::toProduct).toList();

		if (!toInsert.isEmpty()) {
			productRepository.saveAll(toInsert);
		}
	}

	@Transactional
	private Product toProduct(ProductResponse response) {
		Supplier supplier = supplierService.getOrCreate(response.getSupplierIdentifier(),
				response.getSupplierLegalName());
		return new Product(response, supplier);
	}
}
