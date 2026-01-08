package com.br.distributors.models;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tbSupplier", uniqueConstraints = @UniqueConstraint(name = "UK_tbSupplier_identifier", columnNames = "identifier"), indexes = @Index(columnList = "identifier", name = "IDX_SUPPLIER_IDENTIFIER"))
@Schema(name = "Supplier", description = "Fornecedor (baseado nos campos existentes em ProductResponse)")
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@Schema(description = "Identificador do fornecedor (layout X(18); conteúdo CNPJ/CPF sem máscara)")
	@Column(nullable = false, length = 18, unique = true)
	private String identifier;

	@Schema(description = "Razão social do fornecedor")
	@Column(length = 255)
	private String legalName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_Id_EmailConfig", foreignKey = @ForeignKey(name = "FK_FROM_TBEMAILCONFIG_FOR_TBSUPPLIER"))
	private EmailConfiguration emailConfig;

	public Supplier() {

	}

	public Supplier(String identifier, String legalName) {
		this.identifier = identifier;
		this.legalName = legalName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public Long getId() {
		return id;
	}

	public EmailConfiguration getEmailConfig() {
		return emailConfig;
	}

	public void setEmailConfig(EmailConfiguration emailConfig) {
		this.emailConfig = emailConfig;
	}

}