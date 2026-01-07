package com.br.distributors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.distributors.request.SalesFileResponse;
import com.br.distributors.service.SalesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @Operation(summary = "Importa vendas")
    @ApiResponse(responseCode = "204", description = "Importação processada")
    @PostMapping("/import")
    public ResponseEntity<Void> importSales(@RequestBody @Valid	 SalesFileResponse request) {
        salesService.saveAll(request);
        return ResponseEntity.noContent().build();
    }
}
