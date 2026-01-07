package com.br.distributors.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByBarcode(String sku);

	@Query("select p.barcode from Product p where p.barcode in :barcodes")
	Set<String> findExistingBarcodes(@Param("barcodes") Collection<String> barcodes);
}
