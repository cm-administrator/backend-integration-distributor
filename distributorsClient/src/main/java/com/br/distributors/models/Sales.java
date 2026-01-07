package com.br.distributors.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Sales", description = "Sales record (line D) from VENDA1 file")
public class Sales {

	@Schema(description = "Distributor CNPJ (14 digits)", example = "16929282000146")
	public String distributorIdentifier;

	@Schema(description = "Customer ID (14 digits in the file)", example = "11285151000150")
	public String customerIdentifier;

	@Schema(description = "Transaction date (YYYYMMDD)", example = "2025-12-06")
	public LocalDate transactionDate;

	@Schema(description = "Sequence (6 digits after the date in the dataSeq token)", example = "637905")
	public String sequence;

	@Schema(description = "EAN/Product code (variable length)", example = "7896629630277")
	public String productBarcode;

	@Schema(description = "Quantity (4 decimal places)", example = "8.0000")
	public BigDecimal quantity;

	@Schema(description = "Sale price (2 decimal places), extracted from price+salesperson", example = "20.51")
	public BigDecimal salePrice;

	@Schema(description = "Salesperson code (last 4 digits of the price+salesperson field)", example = "4700")
	public String salespersonCode;

	@Schema(description = "Document (e.g., N78785-000 / B78855-000)", example = "N78785-000")
	public String document;

	@Schema(description = "Final field 1 (e.g., 00000000)", example = "00000000")
	public String field7;

	@Schema(description = "Final field 2 (e.g., 00000.000001)", example = "00000.000001")
	public String field8;
}
