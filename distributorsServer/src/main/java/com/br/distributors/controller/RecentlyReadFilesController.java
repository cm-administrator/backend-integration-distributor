package com.br.distributors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.distributors.models.RecentlyReadFiles;
import com.br.distributors.request.BatchUpdateRequest;
import com.br.distributors.response.BatchUpdateResult;
import com.br.distributors.service.RecentlyReadFilesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/recently-read-files")
public class RecentlyReadFilesController {

	private final RecentlyReadFilesService recentlyReadFilesService;

	public RecentlyReadFilesController(RecentlyReadFilesService recentlyReadFilesService) {
		this.recentlyReadFilesService = recentlyReadFilesService;
	}

	@GetMapping("/{distributorIdentifier}")
	@Operation(summary = "Busca os últimos arquivos lidos por identifier do distribuidor")
	@ApiResponse(responseCode = "200", description = "Encontrou o registro")
	@ApiResponse(responseCode = "404", description = "Não existe registro para o distribuidor informado")
	public ResponseEntity<RecentlyReadFiles> getByDistributorIdentifier(@PathVariable String distributorIdentifier) {
		return recentlyReadFilesService.findBydistributorIdentifier(distributorIdentifier).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Operation(summary = "Atualiza checkpoints em lote (somente se o timestamp for mais novo)")
	@ApiResponse(responseCode = "200", description = "Processado com sucesso")
	@PostMapping("/{identifier}/batch-if-newer")
	public ResponseEntity<BatchUpdateResult> batchIfNewer(@PathVariable("identifier") String distributorIdentifier,
			@RequestBody BatchUpdateRequest request) {
		return ResponseEntity.ok(recentlyReadFilesService.updateIfNewerBatch(distributorIdentifier, request));
	}

}
