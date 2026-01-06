package com.br.distributors.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "tbDistributor", uniqueConstraints = @UniqueConstraint(name = "UK_tbDistributor_identifier", columnNames = "identifier"))
@Schema(name = "Distributor", description = "Distribuidor / agente distribuidor (baseado nos campos distributorIdentifier/distributorAgentIdentifier)")
public class Distributor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Chave primária interna", example = "1")
	private Long id;

	@Schema(description = "CNPJ do distribuidor (sem máscara)")
	@Column(nullable = false, length = 14, unique = true)
	private String identifier;

	@Schema(description = "Razão social (se o sistema fornecer em algum ponto)")
	@Column(length = 255)
	private String legalName;

	public Distributor() {

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

}
