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
	public List<CustomerResponse> customerResponses;

	public void setHeaderType(String headerType) {
		this.headerType = headerType;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setSupplierIdentifier(String supplierIdentifier) {
		this.supplierIdentifier = supplierIdentifier;
	}

	public void setCustomerResponses(List<CustomerResponse> customerResponses) {
		this.customerResponses = customerResponses;
	}

}
