package com.br.distributors.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.br.distributors.request.SalesResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbSales", indexes = {
		@Index(name = "IX_tbSales_Dedupe", columnList = "fk_Id_Customer,fk_Id_Distributor,fk_Id_Produto,transactionDate,sequence") })
@Schema(name = "SalesResponse", description = "SalesResponse record (line D) from VENDA1 file")
public class Sales {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "fk_Id_Customer", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_FROM_TBCUSTOMER_FOR_TBSALES"))
	@Schema(description = "Cliente")
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "fk_Id_Produto", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_FROM_TBPRODUCT_FOR_TBSALES"))
	@Schema(description = "Produto")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "fk_Id_SalesPerson", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_FROM_TBSALESPERSON_FOR_TBSALES"))
	@Schema(description = "Vendedor")
	private SalesPerson salesPerson;

	@Schema(description = "Distribuidor / agente distribuidor")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_Id_Distributor", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBDISTRIBUTOR_FOR_TBSALES"))
	private Distributor distributor;

	@Schema(description = "Transaction date (YYYYMMDD)", example = "2025-12-06")
	@Column
	private LocalDate transactionDate;

	@Schema(description = "Sequence (6 digits after the date in the dataSeq token)", example = "637905")
	public String sequence;

	@Schema(description = "Quantity (4 decimal places)", example = "8.0000")
	@Column
	private BigDecimal quantity;

	@Schema(description = "Sale price (2 decimal places), extracted from price+salesperson", example = "20.51")
	@Column
	private BigDecimal salePrice;

	@Schema(description = "Tipo de venda extraído do campo documento (ex: N)")
	public String saleType;

	@Schema(description = "CEP do cliente extraído do campo documento (ex: 78600-000)")
	public String zipCodeCustomer;

	@Schema(description = "Data da integração da venda (AAAAMMDD)")
	@Column
	private LocalDateTime integrationDate;

	@Schema(description = "Data da atualização da venda (AAAAMMDD)")
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

	public Sales() {

	}

	public Sales(SalesResponse response, Customer customer, Product product, SalesPerson salesPerson,
			Distributor distributor) {

		this.customer = customer;
		this.product = product;
		this.salesPerson = salesPerson;
		this.distributor = distributor;
		this.transactionDate = response.getTransactionDate();
		this.quantity = response.getQuantity();
		this.salePrice = response.getSalePrice();
		this.saleType = response.getSaleType();
		this.zipCodeCustomer = response.getZipCodeCustomer();
		this.sequence = response.getSequence();

	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public SalesPerson getSalesPerson() {
		return salesPerson;
	}

	public void setSalesPerson(SalesPerson salesPerson) {
		this.salesPerson = salesPerson;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public String getSaleType() {
		return saleType;
	}

	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}

	public String getZipCodeCustomer() {
		return zipCodeCustomer;
	}

	public void setZipCodeCustomer(String zipCodeCustomer) {
		this.zipCodeCustomer = zipCodeCustomer;
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

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}
