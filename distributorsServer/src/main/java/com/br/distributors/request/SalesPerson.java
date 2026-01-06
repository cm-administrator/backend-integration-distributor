package com.br.distributors.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class SalesPerson {

	@Schema(description = "Distributor agent CNPJ (14 digits).")
	public String distributorAgentIdentifier;

	@Schema(description = "CustomerResponse identification (X(18) - CNPJ/CPF without mask).")
	public String customerIdentifier;

	@Schema(description = "Sales manager code (X(13)).")
	public String salesManagerCode;

	@Schema(description = "Sales manager name (X(50)).")
	public String salesManagerName;

	@Schema(description = "Sales supervisor code (X(13)).")
	public String salesSupervisorCode;

	@Schema(description = "Sales supervisor name (X(50)).")
	public String salesSupervisorName;

	@Schema(description = "Salesperson code (X(20)).")
	public String salespersonCode;

	@Schema(description = "Salesperson name (X(50)).")
	public String salespersonName;
}
