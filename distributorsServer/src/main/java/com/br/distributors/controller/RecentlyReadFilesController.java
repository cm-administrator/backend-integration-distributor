package com.br.distributors.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.br.distributors.models.RecentlyReadFiles;
import com.br.distributors.service.RecentlyReadFilesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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

	@PostMapping("/{distributorIdentifier}/customers")
	@Operation(summary = "Atualiza o último arquivo de clientes se o informado for mais recente")
	@ApiResponse(responseCode = "200", description = "Atualização avaliada com sucesso")
	public ResponseEntity<Map<String, Object>> updateCustomers(@PathVariable String distributorIdentifier,
			@RequestBody UpdateFileRequest request) {
		boolean updated = recentlyReadFilesService.updateCustomersIfNewer(distributorIdentifier, request.fileName(),
				request.fileInstant());
		return ResponseEntity.ok(Map.of("updated", updated));
	}

	@PostMapping("/{distributorIdentifier}/sales")
	@Operation(summary = "Atualiza o último arquivo de vendas se o informado for mais recente")
	@ApiResponse(responseCode = "200", description = "Atualização avaliada com sucesso")
	public ResponseEntity<Map<String, Object>> updateSales(@PathVariable String distributorIdentifier,
			@RequestBody UpdateFileRequest request) {
		boolean updated = recentlyReadFilesService.updateSalesIfNewer(distributorIdentifier, request.fileName(),
				request.fileInstant());
		return ResponseEntity.ok(Map.of("updated", updated));
	}

	@PostMapping("/{distributorIdentifier}/stock")
	@Operation(summary = "Atualiza o último arquivo de estoque se o informado for mais recente")
	@ApiResponse(responseCode = "200", description = "Atualização avaliada com sucesso")
	public ResponseEntity<Map<String, Object>> updateStock(@PathVariable String distributorIdentifier,
			@RequestBody UpdateFileRequest request) {
		boolean updated = recentlyReadFilesService.updateStockIfNewer(distributorIdentifier, request.fileName(),
				request.fileInstant());
		return ResponseEntity.ok(Map.of("updated", updated));
	}

	@PostMapping("/{distributorIdentifier}/products")
	@Operation(summary = "Atualiza o último arquivo de produtos se o informado for mais recente")
	@ApiResponse(responseCode = "200", description = "Atualização avaliada com sucesso")
	public ResponseEntity<Map<String, Object>> updateProducts(@PathVariable String distributorIdentifier,
			@RequestBody UpdateFileRequest request) {
		boolean updated = recentlyReadFilesService.updateProductsIfNewer(distributorIdentifier, request.fileName(),
				request.fileInstant());
		return ResponseEntity.ok(Map.of("updated", updated));
	}

	@PostMapping("/{distributorIdentifier}/sales-person")
	@Operation(summary = "Atualiza o último arquivo de vendedores/força de vendas se o informado for mais recente")
	@ApiResponse(responseCode = "200", description = "Atualização avaliada com sucesso")
	public ResponseEntity<Map<String, Object>> updateSalesPerson(@PathVariable String distributorIdentifier,
			@RequestBody UpdateFileRequest request) {
		boolean updated = recentlyReadFilesService.updateSalesPersonIfNewer(distributorIdentifier, request.fileName(),
				request.fileInstant());
		return ResponseEntity.ok(Map.of("updated", updated));
	}

	@Schema(description = "Payload para atualizar o último arquivo lido de um tipo")
	public static record UpdateFileRequest(
			@Schema(description = "Nome do arquivo (ex: CLIENTESCAN05012026135316893.txt)", example = "CLIENTESCAN05012026135316893.txt") String fileName,
			@Schema(description = "Instant do arquivo (UTC). Ex: 2026-01-05T16:53:28.215Z", example = "2026-01-05T16:53:28.215Z") Instant fileInstant) {
	}
}
