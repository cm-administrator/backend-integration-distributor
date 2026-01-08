package com.br.distributors.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado do processamento: true quando aquele tipo foi atualizado")
public record BatchUpdateResponse(boolean customersUpdated, boolean productUpdated, boolean salesPersonUpdated,
		boolean stockUpdated, boolean salesUpdated) {
}