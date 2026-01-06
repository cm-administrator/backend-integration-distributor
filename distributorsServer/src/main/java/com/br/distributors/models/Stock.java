package com.br.distributors.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbStock")
@Schema(description = "Detail record type (must be 'E').")
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "productCode", referencedColumnName = "barcode", foreignKey = @ForeignKey(name = "FK_FROM_TBPRODUCT_FOR_TBSTOCK"))
	@Schema(description = "Produto (por código de barras)")
	private Product product;

	@Schema(description = "Detail record type (must be 'E').")
	@Column
	private String recordType;

	@Schema(description = "Distribuidor / agente distribuidor")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_Id_Distributor", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBDISTRIBUTOR_FOR_TBSTOCK"))
	private Distributor distributor;

	@Schema(description = "Stock quantity.")
	@Column
	private BigDecimal quantity;

	@Schema(description = "Batch code (if exists).")
	@Column
	private String batchCode;

	@Schema(description = "Batch expiration date (YYYYMMDD) if exists.")
	@Column
	private LocalDate batchExpirationDate;

	@Schema(description = "Data posição do estoque (AAAAMMDD)")
	@Column
	private LocalDate stockDate;

	@Schema(description = "Data posição do estoque (AAAAMMDD)")
	@Column
	private LocalDateTime integrationDate;

	@Schema(description = "Data da atualização do estoque (AAAAMMDD)")
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

	public Stock() {

	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public LocalDate getBatchExpirationDate() {
		return batchExpirationDate;
	}

	public void setBatchExpirationDate(LocalDate batchExpirationDate) {
		this.batchExpirationDate = batchExpirationDate;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getStockDate() {
		return stockDate;
	}

	public void setStockDate(LocalDate stockDate) {
		this.stockDate = stockDate;
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
