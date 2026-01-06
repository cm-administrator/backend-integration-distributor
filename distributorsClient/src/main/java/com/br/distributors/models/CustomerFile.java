package com.br.distributors.models;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class CustomerFile {

	@Schema(description = "Tipo do header (sempre 'H').")
	public String headerType;

	@Schema(description = "Identificador do layout (esperado 'PDV10').")
	public String identifier;

	@Schema(description = "CNPJ do fornecedor (14 dígitos, sem máscara).")
	public String supplierIdentifier;

	@Schema(description = "Lista de clientes (registros 'D').")
	public List<Customer > customers;
}
