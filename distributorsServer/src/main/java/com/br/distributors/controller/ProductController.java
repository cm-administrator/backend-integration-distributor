package com.br.distributors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.distributors.models.Product;
import com.br.distributors.request.ProductFileResponse;
import com.br.distributors.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@Operation(summary = "Busca produto por ID")
	@ApiResponse(responseCode = "200", description = "Produto encontrado", content = @Content(schema = @Schema(implementation = Product.class)))
	@ApiResponse(responseCode = "404", description = "Produto não encontrado")
	@GetMapping("/{id}")
	public ResponseEntity<Product> findById(@PathVariable("id") Long id) {

		return ResponseEntity.ok(productService.findById(id));

	}

	@Operation(summary = "Busca produto por EAN (barcode)")
	@ApiResponse(responseCode = "200", description = "Produto encontrado", content = @Content(schema = @Schema(implementation = Product.class)))
	@ApiResponse(responseCode = "404", description = "Produto não encontrado")
	@GetMapping("/barcode/{barcode}")
	public ResponseEntity<Product> getByBarcode(@PathVariable("barcode") String barcode) {

		return ResponseEntity.ok(productService.getByBarcode(barcode));

	}

	@Operation(summary = "Importa produtos (insere apenas os que não existem)")
	@ApiResponse(responseCode = "204", description = "Importação processada")
	@PostMapping("/import")
	public ResponseEntity<Void> importProducts(@RequestBody ProductFileResponse request) {
		productService.saveAll(request);
		return ResponseEntity.noContent().build();
	}

}
