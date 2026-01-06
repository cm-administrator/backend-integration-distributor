package com.br.distributors.models;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public class Customer {

	@Schema(description = "Distributor agent CNPJ (14 digits).")
	public String distributorAgentIdentifier;

	@Schema(description = "Customer identification (X(18) - CNPJ/CPF without mask).")
	public String customerIdentifier;

	@Schema(description = "Customer legal name (X(40)).")
	public String legalName;

	@Schema(description = "Address (street) (X(40)).")
	public String address;

	@Schema(description = "Neighborhood (X(30)).")
	public String neighborhood;

	@Schema(description = "ZIP code (9(5)-9(3)).")
	public String zipCode;

	@Schema(description = "City (X(30)).")
	public String city;

	@Schema(description = "State (2-character abbreviation).")
	public String state;

	@Schema(description = "Responsible person's name (X(20)).")
	public String responsibleName;

	@Schema(description = "Phone numbers (X(40)).")
	public String phoneNumbers;

	@Schema(description = "Customer CNPJ/CPF (X(18)).")
	public String customerIdentifier2;

	@Schema(description = "Route (X(10)).")
	public String route;

	@Schema(description = "Store type (X(10)).")
	public String storeType;

	@Schema(description = "Representativity (9(3).9(2)).")
	public BigDecimal representativity;

}
