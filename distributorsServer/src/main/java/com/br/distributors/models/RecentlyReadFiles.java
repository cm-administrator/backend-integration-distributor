package com.br.distributors.models;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "tbRecentlyReadFiles")
@Schema(description = "Armazena o último arquivo lido por tipo, por distribuidor, para evitar reprocessamento")
public class RecentlyReadFiles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "ID do registro")
	private Long id;

	@Schema(description = "Nome do último arquivo de clientes processado")
	@Column(length = 255)
	private String customersFile;

	@Schema(description = "Timestamp do último arquivo de clientes (extraído do nome ou lastModified)")
	private Instant customersInstant;

	@Schema(description = "Nome do último arquivo de vendas processado")
	@Column(length = 255)
	private String salesFile;

	@Schema(description = "Timestamp do último arquivo de vendas")
	private Instant salesInstant;

	@Schema(description = "Nome do último arquivo de estoque processado")
	@Column(length = 255)
	private String stockFile;

	@Schema(description = "Timestamp do último arquivo de estoque")
	private Instant stockInstant;

	@Schema(description = "Nome do último arquivo de produtos processado")
	@Column(length = 255)
	private String productFile;

	@Schema(description = "Timestamp do último arquivo de produtos")
	private Instant productInstant;

	@Schema(description = "Nome do último arquivo de vendedores/força de vendas processado")
	@Column(length = 255)
	private String salesPersonFile;

	@Schema(description = "Timestamp do último arquivo de vendedores/força de vendas")
	private Instant salesPersonInstant;

	@JsonIgnore
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_Id_Distributor", nullable = false, foreignKey = @ForeignKey(name = "FK_FROM_TBDISTRIBUTOR_FOR_TBRECENTLYREADFILES"))
	private Distributor distributor;

	@CreationTimestamp
	@Schema(description = "Data/hora de criação do registro")
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@Schema(description = "Data/hora da última atualização do registro")
	private LocalDateTime updatedDate;

	public RecentlyReadFiles() {
	}

	public RecentlyReadFiles(Distributor distributor) {
		this.distributor = distributor;
	}

	public RecentlyReadFiles(Distributor distributor, RecentlyReadFiles base) {
		this(distributor);

		this.setCustomersFile(base.getCustomersFile());
		this.setCustomersInstant(base.getCustomersInstant());

		this.setSalesFile(base.getSalesFile());
		this.setSalesInstant(base.getSalesInstant());

		this.setStockFile(base.getStockFile());
		this.setStockInstant(base.getStockInstant());

		this.setProductFile(base.getProductFile());
		this.setProductInstant(base.getProductInstant());

		this.setSalesPersonFile(base.getSalesPersonFile());
		this.setSalesPersonInstant(base.getSalesPersonInstant());
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createdDate = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getCustomersFile() {
		return customersFile;
	}

	public void setCustomersFile(String customersFile) {
		this.customersFile = customersFile;
	}

	public Instant getCustomersInstant() {
		return customersInstant;
	}

	public void setCustomersInstant(Instant customersInstant) {
		this.customersInstant = customersInstant;
	}

	public String getSalesFile() {
		return salesFile;
	}

	public void setSalesFile(String salesFile) {
		this.salesFile = salesFile;
	}

	public Instant getSalesInstant() {
		return salesInstant;
	}

	public void setSalesInstant(Instant salesInstant) {
		this.salesInstant = salesInstant;
	}

	public String getStockFile() {
		return stockFile;
	}

	public void setStockFile(String stockFile) {
		this.stockFile = stockFile;
	}

	public Instant getStockInstant() {
		return stockInstant;
	}

	public void setStockInstant(Instant stockInstant) {
		this.stockInstant = stockInstant;
	}

	public String getProductFile() {
		return productFile;
	}

	public void setProductFile(String productFile) {
		this.productFile = productFile;
	}

	public Instant getProductInstant() {
		return productInstant;
	}

	public void setProductInstant(Instant productInstant) {
		this.productInstant = productInstant;
	}

	public String getSalesPersonFile() {
		return salesPersonFile;
	}

	public void setSalesPersonFile(String salesPersonFile) {
		this.salesPersonFile = salesPersonFile;
	}

	public Instant getSalesPersonInstant() {
		return salesPersonInstant;
	}

	public void setSalesPersonInstant(Instant salesPersonInstant) {
		this.salesPersonInstant = salesPersonInstant;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}
}
