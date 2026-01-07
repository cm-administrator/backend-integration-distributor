package com.br.distributors.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Customer;
import com.br.distributors.models.Distributor;
import com.br.distributors.repository.CustomerRepository;
import com.br.distributors.request.CustomerFileResponse;
import com.br.distributors.request.CustomerResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final DistributorService distributorService;

	public CustomerService(CustomerRepository customerRepository, DistributorService distributorService) {
		this.customerRepository = customerRepository;
		this.distributorService = distributorService;
	}

	@Transactional(readOnly = true)
	public Customer findById(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado para ID: " + customerId));
	}

	@Transactional(readOnly = true)
	public List<Customer> findAllByIdentifiers(Set<String> identifiers) {
		return customerRepository.findAllByIdentifierIn(identifiers);
	}

	@Transactional(readOnly = true)
	private Optional<Customer> findByIdentifier(String identifier) {
		return customerRepository.findByIdentifier(identifier);
	}

	public Customer getByIdentifier(String identifier) {
		return findByIdentifier(identifier).orElseThrow(
				() -> new EntityNotFoundException("Clienet não encontrado para identificador:" + identifier));
	}

	/**
	 * Insere apenas clientes ainda inexistentes (por identifier). Evita N queries
	 * (uma por item) buscando todos os existentes de uma vez.
	 */
	@Transactional
	public void saveAll(CustomerFileResponse response) {
		List<CustomerResponse> items = response.getCustomers();
		if (items.isEmpty())
			return;

		Set<String> identifiers = items.stream().map(CustomerResponse::getIdentifier).filter(Objects::nonNull)
				.map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		if (identifiers.isEmpty())
			return;

		Set<String> existing = customerRepository.findExistingIdentifiers(identifiers);

		List<Customer> toInsert = items.stream().filter(r -> r.getIdentifier() != null && !r.getIdentifier().isBlank())
				.filter(r -> !existing.contains(r.getIdentifier().trim())).map(this::toCustomer).toList();

		if (!toInsert.isEmpty()) {
			customerRepository.saveAll(toInsert);
		}
	}

	private Customer toCustomer(CustomerResponse response) {
		Distributor distributor = distributorService.getOrCreate(response.getDistributorAgentIdentifier(), null);
		return new Customer(response, distributor);
	}

}
