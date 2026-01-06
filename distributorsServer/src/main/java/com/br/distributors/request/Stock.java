package com.br.distributors.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public class Stock {

	@Schema(description = "Detail record type (must be 'E').")
	public String recordType;

	@Schema(description = "Distributor agent CNPJ without mask.")
	public String distributorAgentIdentifier;

	@Schema(description = "Product code (manual: X(14) - EAN13/DUN14/SKU; may vary in file).")
	public String productCode;

	@Schema(description = "Stock quantity.")
	public BigDecimal quantity;

	@Schema(description = "Batch code (if exists).")
	public String batchCode;

	@Schema(description = "Batch expiration date (YYYYMMDD) if exists.")
	public LocalDate batchExpirationDate;
}
