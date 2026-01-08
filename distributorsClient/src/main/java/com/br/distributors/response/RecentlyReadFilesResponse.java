package com.br.distributors.response;

import java.time.Instant;

public class RecentlyReadFilesResponse {
	private Long id;

	private String customersFile;
	private Instant customersInstant;

	private String salesFile;
	private Instant salesInstant;

	private String stockFile;
	private Instant stockInstant;

	private String productFile;
	private Instant productInstant;

	private String salesPersonFile;
	private Instant salesPersonInstant;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "RecentlyReadFilesResponse [id=" + id + ", customersFile=" + customersFile + ", customersInstant="
				+ customersInstant + ", salesFile=" + salesFile + ", salesInstant=" + salesInstant + ", stockFile="
				+ stockFile + ", stockInstant=" + stockInstant + ", productFile=" + productFile + ", productInstant="
				+ productInstant + ", salesPersonFile=" + salesPersonFile + ", salesPersonInstant=" + salesPersonInstant
				+ "]";
	}

}