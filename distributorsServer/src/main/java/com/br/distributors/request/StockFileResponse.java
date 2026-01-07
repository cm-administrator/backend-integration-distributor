package com.br.distributors.request;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class StockFileResponse {

	@Schema(description = "Tipo do registro do header (deve ser 'H')")
	public String headerType;

	@Schema(description = "Identificador do arquivo (no manual: 'ESTOQ09')")
	public String identifier;

	@Schema(description = "CNPJ do fornecedor (fabricante) sem máscara")
	public String supplierIdentifier;

	@Schema(description = "Data posição do estoque (AAAAMMDD)")
	public LocalDate stockDate;

	@Schema(description = "Itens/linhas de estoque (registros 'E')")
	public List<StockResponse> items;

	public String getHeaderType() {
		return headerType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getSupplierIdentifier() {
		return supplierIdentifier;
	}

	public LocalDate getStockDate() {
		return stockDate;
	}

	public List<StockResponse> getItems() {
		return items;
	}

}
