package com.br.distributors.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbSalesPerson")
@Schema(description = "Distributor agent CNPJ (14 digits).")
public class SalesPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@Schema(description = "Distribuidor / agente distribuidor")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_Id_Distributor", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBDISTRIBUTOR_FOR_TBSALESPERSON"))
	private Distributor distributor;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tbSalesPersonCustomer", joinColumns = @JoinColumn(name = "salesPersonId", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBSALESPERSON_FOR_TBSALESPERSONCUSTOMER")), inverseJoinColumns = @JoinColumn(name = "customerId", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBCUSTOMER_FOR_TBSALESPERSONCUSTOMER")))
	@Schema(description = "Clientes desse vendedor")
	private Set<Customer> customers = new HashSet<>();;

	@Schema(description = "Sales manager code (X(13)).")
	@Column
	private String salesManagerCode;

	@Schema(description = "Sales manager name (X(50)).")
	@Column
	private String salesManagerName;

	@Schema(description = "Sales supervisor code (X(13)).")
	@Column
	private String salesSupervisorCode;

	@Schema(description = "Sales supervisor name (X(50)).")
	@Column
	private String salesSupervisorName;

	@Schema(description = "Salesperson code (X(20)).")
	@Column
	private String salespersonCode;

	@Schema(description = "Salesperson name (X(50)).")
	@Column
	private String salespersonName;
	@Schema(description = "Data posição do vendedor (AAAAMMDD)")
	@Column
	private LocalDateTime integrationDate;

	@Schema(description = "Data da atualização do vendedor (AAAAMMDD)")
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

	public SalesPerson() {

	}

	public Long getId() {
		return id;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public Set<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(Set<Customer> customers) {
		this.customers = customers;
	}

	public String getSalesManagerCode() {
		return salesManagerCode;
	}

	public void setSalesManagerCode(String salesManagerCode) {
		this.salesManagerCode = salesManagerCode;
	}

	public String getSalesManagerName() {
		return salesManagerName;
	}

	public void setSalesManagerName(String salesManagerName) {
		this.salesManagerName = salesManagerName;
	}

	public String getSalesSupervisorCode() {
		return salesSupervisorCode;
	}

	public void setSalesSupervisorCode(String salesSupervisorCode) {
		this.salesSupervisorCode = salesSupervisorCode;
	}

	public String getSalesSupervisorName() {
		return salesSupervisorName;
	}

	public void setSalesSupervisorName(String salesSupervisorName) {
		this.salesSupervisorName = salesSupervisorName;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public String getSalespersonName() {
		return salespersonName;
	}

	public void setSalespersonName(String salespersonName) {
		this.salespersonName = salespersonName;
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
