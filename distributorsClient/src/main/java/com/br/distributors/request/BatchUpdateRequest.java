package com.br.distributors.request;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição em lote contendo checkpoints por tipo (campos opcionais)")
public record BatchUpdateRequest(String customersFile, Instant customersInstant, String productFile,
		Instant productInstant, String salesPersonFile, Instant salesPersonInstant, String stockFile,
		Instant stockInstant, String salesFile, Instant salesInstant) {
}