package com.br.distributors.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class CustomerFileResponse {

	@Schema(description = "Tipo do header (sempre 'H').")
	public String headerType;

	@Schema(description = "Identificador do layout (esperado 'PDV10').")
	public String identifier;

	@Schema(description = "CNPJ do fornecedor (14 dígitos, sem máscara).")
	public String supplierIdentifier;

	@Schema(description = "Lista de clientes (registros 'D').")
	public List<CustomerResponse> customers;

	public String getHeaderType() {
		return headerType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getSupplierIdentifier() {
		return supplierIdentifier;
	}

	public List<CustomerResponse> getCustomers() {
		return customers;
	}

}
