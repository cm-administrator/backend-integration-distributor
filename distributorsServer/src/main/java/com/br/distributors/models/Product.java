package com.br.distributors.models;

import java.time.LocalDateTime;

import com.br.distributors.request.ProductResponse;

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
@Table(name = "tbProduct", indexes = @Index(columnList = "barcode", name = "IDX_PRODUCT_BARCODE"))
@Schema(description = "Supplier identifier (layout field X(18); content is CNPJ without mask).")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@Schema(description = "Fornecedor")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_Id_Supplier", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBSUPPLIER_FOR_TBPRODUCT"))
	private Supplier supplier;

	@Schema(description = "ProductResponse internal code at the distributor agent (X(14)).")
	@Column
	private String code;

	@Schema(description = "Packaging type: '0' non-fractioned, '1' fractioned.")
	@Column
	private String packagingType;

	@Schema(description = "Barcode (EAN13 or DUN14) - X(14).")
	@Column
	private String barcode;

	@Schema(description = "Barcode type: '1' EAN13, '2' DUN14, '3' others.")
	@Column
	private String barcodeType;

	@Schema(description = "ProductResponse name/presentation (X(100)).")
	@Column
	private String name;

	@Schema(description = "ProductResponse division (X(40)).")
	@Column
	private String division;

	@Schema(description = "ProductResponse status: 'A' active, 'I' inactive.")
	@Column
	private String status;

	@Schema(description = "Data integração do produto (AAAAMMDD)")
	@Column
	private LocalDateTime integrationDate;

	@Schema(description = "Data da atualização do produto (AAAAMMDD)")
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

	public Product() {
	}

	public Product(ProductResponse response, Supplier supplier) {
		this.supplier = supplier;
		this.code = response.getCode();
		this.packagingType = response.getPackagingType();
		this.barcode = response.getBarcode();
		this.barcodeType = response.getBarcodeType();
		this.name = response.getName();
		this.division = response.getDivision();
		this.status = response.getStatus();
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPackagingType() {
		return packagingType;
	}

	public void setPackagingType(String packagingType) {
		this.packagingType = packagingType;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBarcodeType() {
		return barcodeType;
	}

	public void setBarcodeType(String barcodeType) {
		this.barcodeType = barcodeType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
