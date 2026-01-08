package com.br.distributors.response;

public record BatchUpdateResult(boolean customersUpdated, boolean salesUpdated, boolean stockUpdated,
		boolean productUpdated, boolean salesPersonUpdated) {
	public static BatchUpdateResult none() {
		return new BatchUpdateResult(false, false, false, false, false);
	}
}