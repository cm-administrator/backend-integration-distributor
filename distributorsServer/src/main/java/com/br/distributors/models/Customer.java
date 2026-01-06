package com.br.distributors.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import nonapi.io.github.classgraph.json.Id;

@Entity
@Table(name = "tbCustomer")
@Schema(description = "Distributor agent CNPJ (14 digits).")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@Schema(description = "Distribuidor / agente distribuidor")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_Id_Distributor", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBDISTRIBUTOR_FOR_TBCUSTOMER"))
	private Distributor distributor;

	@Schema(description = "Customer identification (X(18) - CNPJ/CPF without mask).")
	@Column
	private String customerIdentifier;

	@Schema(description = "Customer legal name (X(40)).")
	@Column
	private String legalName;

	@Schema(description = "Address (street) (X(40)).")
	@Column
	private String address;

	@Schema(description = "Neighborhood (X(30)).")
	@Column
	private String neighborhood;

	@Schema(description = "ZIP code (9(5)-9(3)).")
	@Column
	private String zipCode;

	@Schema(description = "City (X(30)).")
	@Column
	private String city;

	@Schema(description = "State (2-character abbreviation).")
	@Column
	private String state;

	@Schema(description = "Responsible person's name (X(20)).")
	@Column
	private String responsibleName;

	@Schema(description = "Phone numbers (X(40)).")
	@Column
	private String phoneNumbers;

	@Schema(description = "Customer CNPJ/CPF (X(18)).")
	@Column
	private String customerIdentifier2;

	@Schema(description = "Route (X(10)).")
	@Column
	private String route;

	@Schema(description = "Store type (X(10)).")
	@Column
	private String storeType;

	@Schema(description = "Representativity (9(3).9(2)).")
	@Column
	private BigDecimal representativity;

	@Schema(description = "Data da integração do cliente (AAAAMMDD)")
	@Column
	private LocalDateTime integrationDate;

	@Schema(description = "Data da atualização do cliente (AAAAMMDD)")
	@Column
	private LocalDateTime updateDate;

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.integrationDate = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updateDate = LocalDateTime.now();
	}

	public Customer() {

	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public String getCustomerIdentifier() {
		return customerIdentifier;
	}

	public void setCustomerIdentifier(String customerIdentifier) {
		this.customerIdentifier = customerIdentifier;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getResponsibleName() {
		return responsibleName;
	}

	public void setResponsibleName(String responsibleName) {
		this.responsibleName = responsibleName;
	}

	public String getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(String phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public String getCustomerIdentifier2() {
		return customerIdentifier2;
	}

	public void setCustomerIdentifier2(String customerIdentifier2) {
		this.customerIdentifier2 = customerIdentifier2;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public BigDecimal getRepresentativity() {
		return representativity;
	}

	public void setRepresentativity(BigDecimal representativity) {
		this.representativity = representativity;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getIntegrationDate() {
		return integrationDate;
	}

	public void setIntegrationDate(LocalDateTime integrationDate) {
		this.integrationDate = integrationDate;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
	}

}
