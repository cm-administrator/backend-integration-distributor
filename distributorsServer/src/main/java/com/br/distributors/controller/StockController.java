package com.br.distributors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.br.distributors.models.Stock;
import com.br.distributors.request.StockFileResponse;
import com.br.distributors.service.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/stocks")
public class StockController {

	private final StockService stockService;

	public StockController(StockService stockService) {
		this.stockService = stockService;
	}

	@Operation(summary = "Busca estoque por ID")
	@ApiResponse(responseCode = "200", description = "Estoque encontrado", content = @Content(schema = @Schema(implementation = Stock.class)))
	@ApiResponse(responseCode = "404", description = "Estoque não encontrado")
	@GetMapping("/{id}")
	public Stock findById(@PathVariable("id") Long id) {
		return stockService.findById(id);
	}

	@Operation(summary = "Busca estoque por EAN e identificador do distribuidor")
	@ApiResponse(responseCode = "200", description = "Estoque encontrado", content = @Content(schema = @Schema(implementation = Stock.class)))
	@ApiResponse(responseCode = "404", description = "Estoque não encontrado")
	@GetMapping("/barcode/{barcode}/distributor/{distributorIdentifier}")
	public Stock getByBarcodeAndDistributorIdentifier(@PathVariable("barcode") String barcode,
			@PathVariable("distributorIdentifier") String distributorIdentifier) {
		return stockService.getByBarcodeAndDistributorIdentifier(barcode, distributorIdentifier);
	}

	@Operation(summary = "Importa estoque (insere e atualiza quantity quando a data do arquivo mudar)")
	@ApiResponse(responseCode = "204", description = "Importação processada")
	@PostMapping("/import")
	public ResponseEntity<Void> importStock(@RequestBody StockFileResponse request) {
		stockService.saveAll(request);
		return ResponseEntity.noContent().build();
	}
}
