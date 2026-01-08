package com.br.distributors.request;

import java.time.Instant;

public record BatchUpdateRequest(String customersFile, Instant customersInstant, String salesFile, Instant salesInstant,
		String stockFile, Instant stockInstant, String productFile, Instant productInstant, String salesPersonFile,
		Instant salesPersonInstant) {
}