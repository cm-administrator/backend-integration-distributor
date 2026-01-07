package com.br.distributors.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.distributors.models.Customer;
import com.br.distributors.request.CustomerFileResponse;
import com.br.distributors.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Operation(summary = "Busca cliente por ID")
	@ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = Customer.class)))
	@ApiResponse(responseCode = "404", description = "Cliente não encontrado")
	@GetMapping("/{id}")
	public ResponseEntity<Customer> findById(@PathVariable("id") Long id) {

		return ResponseEntity.ok(customerService.findById(id));
	}

	@Operation(summary = "Busca clientes por lista de identificadores")
	@ApiResponse(responseCode = "200", description = "Lista de clientes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class))))
	@PostMapping("/by-identifiers")
	public ResponseEntity<List<Customer>> findAllByIdentifiers(@RequestBody Set<String> identifiers) {
		return ResponseEntity.ok(customerService.findAllByIdentifiers(identifiers));
	}

	@Operation(summary = "Busca cliente por identificador")
	@ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = Customer.class)))
	@ApiResponse(responseCode = "404", description = "Cliente não encontrado")
	@GetMapping("/identifier/{identifier}")
	public ResponseEntity<Customer> getByIdentifier(@PathVariable("identifier") String identifier) {

		return ResponseEntity.ok(customerService.getByIdentifier(identifier));

	}

	@Operation(summary = "Importa clientes (insere apenas os que não existem)")
	@ApiResponse(responseCode = "204", description = "Importação processada")
	@PostMapping("/import")
	public ResponseEntity<Void> importCustomers(@RequestBody CustomerFileResponse request) {
		customerService.saveAll(request);
		return ResponseEntity.noContent().build();
	}
}
