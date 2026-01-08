package com.br.distributors.request;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um arquivo para atualização condicional (se timestamp for mais novo)")
public record UpdateFileRequest(
		@Schema(description = "Nome do arquivo", example = "CLIENTESCAN05012026135328215.TXT") String fileName,
		@Schema(description = "Instante do arquivo (ISO-8601)", example = "2026-01-05T16:53:28.215Z") Instant fileInstant) {
}
