package com.br.distributors.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "SalesResponse", description = "SalesResponse record (line D) from VENDA1 file")
public class SalesResponse {

	@Schema(description = "Distributor CNPJ (14 digits)", example = "16929282000146")
	public String distributorIdentifier;

	@Schema(description = "CustomerResponse ID (14 digits in the file)", example = "11285151000150")
	public String customerIdentifier;

	@Schema(description = "Transaction date (YYYYMMDD)", example = "2025-12-06")
	public LocalDate transactionDate;

	@NotBlank(message = "o EAN do produto não pode ser nulo!")
	@Schema(description = "EAN/ProductResponse code (variable length)", example = "7896629630277")
	public String productBarcode;

	@Schema(description = "Sequence (6 digits after the date in the dataSeq token)", example = "637905")
	public String sequence;

	@Schema(description = "Quantity (4 decimal places)", example = "8.0000")
	public BigDecimal quantity;

	@Schema(description = "Sale price (2 decimal places), extracted from price+salesperson", example = "20.51")
	public BigDecimal salePrice;

	@Schema(description = "Salesperson code (last 4 digits of the price+salesperson field)", example = "4700")
	public String salespersonCode;

	@Schema(description = "Tipo de venda extraído do campo documento (ex: N)")
	public String saleType;

	@Schema(description = "CEP do cliente extraído do campo documento (ex: 78600-000)")
	public String zipCodeCustomer;

	public String getDistributorIdentifier() {
		return distributorIdentifier;
	}

	public String getCustomerIdentifier() {
		return customerIdentifier;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public String getProductBarcode() {
		return productBarcode;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public String getSaleType() {
		return saleType;
	}

	public String getZipCodeCustomer() {
		return zipCodeCustomer;
	}

	public String getSequence() {
		return sequence;
	}

}
