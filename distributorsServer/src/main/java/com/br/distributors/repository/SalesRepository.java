package com.br.distributors.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

	boolean existsByCustomer_IdentifierAndDistributor_IdentifierAndProduct_BarcodeAndTransactionDateAndSequence(
			String customerIdentifier, String distributorIdentifier, String productBarcode, LocalDate transactionDate,
			String sequence);
}
