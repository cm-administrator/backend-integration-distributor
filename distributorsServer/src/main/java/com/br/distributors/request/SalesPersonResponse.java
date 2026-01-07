package com.br.distributors.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class SalesPersonResponse {

	@Schema(description = "Distributor agent CNPJ (14 digits).")
	public String distributorAgentIdentifier;

	@Schema(description = "CustomerResponse identification (X(18) - CNPJ/CPF without mask).")
	public String customerIdentifier;

	@Schema(description = "SalesResponse manager code (X(13)).")
	public String salesManagerCode;

	@Schema(description = "SalesResponse manager name (X(50)).")
	public String salesManagerName;

	@Schema(description = "SalesResponse supervisor code (X(13)).")
	public String salesSupervisorCode;

	@Schema(description = "SalesResponse supervisor name (X(50)).")
	public String salesSupervisorName;

	@Schema(description = "Salesperson code (X(20)).")
	public String salespersonCode;

	@Schema(description = "Salesperson name (X(50)).")
	public String salespersonName;

	public String getDistributorAgentIdentifier() {
		return distributorAgentIdentifier;
	}

	public String getCustomerIdentifier() {
		return customerIdentifier;
	}

	public String getSalesManagerCode() {
		return salesManagerCode;
	}

	public String getSalesManagerName() {
		return salesManagerName;
	}

	public String getSalesSupervisorCode() {
		return salesSupervisorCode;
	}

	public String getSalesSupervisorName() {
		return salesSupervisorName;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public String getSalespersonName() {
		return salespersonName;
	}

}
