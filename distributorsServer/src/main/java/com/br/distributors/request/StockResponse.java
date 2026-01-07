package com.br.distributors.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public class StockResponse {

	@Schema(description = "Detail record type (must be 'E').")
	public String recordType;

	@Schema(description = "Distributor agent CNPJ without mask.")
	public String distributorAgentIdentifier;

	@Schema(description = "ProductResponse code (manual: X(14) - EAN13/DUN14/SKU; may vary in file).")
	public String productBarcode;

	@Schema(description = "StockResponse quantity.")
	public BigDecimal quantity;

	@Schema(description = "Batch code (if exists).")
	public String batchCode;

	@Schema(description = "Batch expiration date (YYYYMMDD) if exists.")
	public LocalDate batchExpirationDate;

	public String getRecordType() {
		return recordType;
	}

	public String getDistributorAgentIdentifier() {
		return distributorAgentIdentifier;
	}

	public String getProductBarcode() {
		return productBarcode;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public LocalDate getBatchExpirationDate() {
		return batchExpirationDate;
	}

}
