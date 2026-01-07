package com.br.distributors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.br.distributors.models.SalesPerson;
import com.br.distributors.request.SalesPersonFileResponse;
import com.br.distributors.service.SalesPersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/salespersons")
public class SalesPersonController {

	private final SalesPersonService salesPersonService;

	public SalesPersonController(SalesPersonService salesPersonService) {
		this.salesPersonService = salesPersonService;
	}

	@Operation(summary = "Busca vendedor por ID")
	@ApiResponse(responseCode = "200", description = "Vendedor encontrado", content = @Content(schema = @Schema(implementation = SalesPerson.class)))
	@ApiResponse(responseCode = "404", description = "Vendedor não encontrado")
	@GetMapping("/{id}")
	public SalesPerson findById(@PathVariable("id") Long id) {
		return salesPersonService.findById(id);
	}

	@Operation(summary = "Busca vendedor por código")
	@ApiResponse(responseCode = "200", description = "Vendedor encontrado", content = @Content(schema = @Schema(implementation = SalesPerson.class)))
	@ApiResponse(responseCode = "404", description = "Vendedor não encontrado")
	@GetMapping("/code/{salespersonCode}")
	public SalesPerson getBySalespersonCode(@PathVariable("salespersonCode") String salespersonCode) {
		return salesPersonService.getBySalespersonCode(salespersonCode);
	}

	@Operation(summary = "Importa vendedores e vincula clientes")
	@ApiResponse(responseCode = "204", description = "Importação processada")
	@PostMapping("/import")
	public ResponseEntity<Void> importAll(@RequestBody SalesPersonFileResponse request) {
		salesPersonService.importAll(request);
		return ResponseEntity.noContent().build();
	}
}
