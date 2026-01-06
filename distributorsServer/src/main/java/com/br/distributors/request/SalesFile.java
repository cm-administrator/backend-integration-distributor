package com.br.distributors.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SalesFile", description = "Arquivo de vendas convertido do TXT (VENDA1)")
public class SalesFile {

	@Schema(description = "Layout no header (ex.: VENDA1)", example = "VENDA1")
	public String layout;

	@Schema(description = "CNPJ do fornecedor no header", example = "14757695000246")
	public String supplierIdentifier;

	@Schema(description = "Itens (linhas D)")
	public List<Sales> items;
}
