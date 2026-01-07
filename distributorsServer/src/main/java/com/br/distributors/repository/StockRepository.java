package com.br.distributors.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

	Optional<Stock> findByProductBarcodeAndDistributorIdentifier(String barcode, String distributorIdentifier);

	List<Stock> findAllByDistributorIdentifierInAndProductBarcodeIn(Set<String> distributorIdentifiers,
			Set<String> barcodes);
}
