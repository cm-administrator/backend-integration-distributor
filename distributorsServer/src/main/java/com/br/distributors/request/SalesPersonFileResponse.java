package com.br.distributors.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class SalesPersonFileResponse {

	@Schema(description = "Tipo do registro do header (deve ser 'H')")
	public String headerType;

	@Schema(description = "Identificador do arquivo (no manual: 'ESTOQ09')")
	public String identifier;

	@Schema(description = "CNPJ do fornecedor (fabricante) sem máscara")
	public String supplierIdentifier;

	@Schema(description = "Registros de força de vendas (linhas 'D').")
	public List<SalesPersonResponse> details;

	public String getHeaderType() {
		return headerType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getSupplierIdentifier() {
		return supplierIdentifier;
	}

	public List<SalesPersonResponse> getDetails() {
		return details;
	}

}
