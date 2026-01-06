package com.br.distributors.models;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class SalesPersonFile {

	@Schema(description = "Tipo do registro do header (deve ser 'H')")
	public String headerType;

	@Schema(description = "Identificador do arquivo (no manual: 'ESTOQ09')")
	public String identifier;

	@Schema(description = "CNPJ do fornecedor (fabricante) sem máscara")
	public String supplierIdentifier;

	@Schema(description = "Registros de força de vendas (linhas 'D').")
	public List<SalesPerson> details;
}
