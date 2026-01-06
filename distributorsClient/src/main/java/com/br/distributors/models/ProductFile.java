package com.br.distributors.models;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProductFile {

	@Schema(description = "Tipo do header (sempre 'H' no arquivo de produtos).")
	public String headerType;

	@Schema(description = "Identificador do layout (esperado 'CADPROD').")
	public String layout;

	@Schema(description = "Data de geração do arquivo (AAAAMMDD).")
	public LocalDate generationDate;

	@Schema(description = "Lista de CNPJ(s) do Agente de Distribuição (registros 'I').")
	public List<String> identifiers;

	@Schema(description = "Lista de produtos (registros 'V').")
	public List<Product> products;
}
