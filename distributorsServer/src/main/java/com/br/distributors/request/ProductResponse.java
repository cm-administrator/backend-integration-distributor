package com.br.distributors.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProductResponse {

	@Schema(description = "Supplier identifier (layout field X(18); content is CNPJ without mask).")
	public String supplierIdentifier;

	@Schema(description = "Supplier legal name.")
	public String supplierLegalName;

	@Schema(description = "ProductResponse internal code at the distributor agent (X(14)).")
	public String code;

	@Schema(description = "Packaging type: '0' non-fractioned, '1' fractioned.")
	public String packagingType;

	@Schema(description = "Barcode (EAN13 or DUN14) - X(14).")
	public String barcode;

	@Schema(description = "Barcode type: '1' EAN13, '2' DUN14, '3' others.")
	public String barcodeType;

	@Schema(description = "ProductResponse name/presentation (X(100)).")
	public String name;

	@Schema(description = "ProductResponse division (X(40)).")
	public String division;

	@Schema(description = "ProductResponse status: 'A' active, 'I' inactive.")
	public String status;

	public String getSupplierIdentifier() {
		return supplierIdentifier;
	}

	public String getSupplierLegalName() {
		return supplierLegalName;
	}

	public String getCode() {
		return code;
	}

	public String getPackagingType() {
		return packagingType;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getBarcodeType() {
		return barcodeType;
	}

	public String getName() {
		return name;
	}

	public String getDivision() {
		return division;
	}

	public String getStatus() {
		return status;
	}

}
