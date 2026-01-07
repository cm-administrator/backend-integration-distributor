package com.br.distributors.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Customer;
import com.br.distributors.models.Distributor;
import com.br.distributors.models.SalesPerson;
import com.br.distributors.repository.SalesPersonRepository;
import com.br.distributors.request.SalesPersonFileResponse;
import com.br.distributors.request.SalesPersonResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SalesPersonService {

	private final SalesPersonRepository salesPersonRepository;
	private final DistributorService distributorService;
	private final CustomerService customerService;

	public SalesPersonService(SalesPersonRepository salesPersonRepository, DistributorService distributorService,
			CustomerService customerService) {
		this.salesPersonRepository = salesPersonRepository;
		this.distributorService = distributorService;
		this.customerService = customerService;
	}

	@Transactional(readOnly = true)
	public SalesPerson findById(Long id) {
		return salesPersonRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Vendedor não encontrado para ID: " + id));
	}

	@Transactional(readOnly = true)
	private Optional<SalesPerson> findBySalespersonCode(String salespersonCode) {
		return salesPersonRepository.findBySalespersonCode(normalizeCode(salespersonCode));
	}

	@Transactional(readOnly = true)
	public SalesPerson getBySalespersonCode(String salespersonCode) {
		return salesPersonRepository.findBySalespersonCode(normalizeCode(salespersonCode)).orElseThrow(
				() -> new EntityNotFoundException("Vendedor não encontrado para codigo: " + salespersonCode));
	}

	@Transactional
	public void importAll(SalesPersonFileResponse file) {
		List<SalesPersonResponse> rows = file.getDetails();

		Map<String, SalesPerson> salesPersonsByCode = upsertSalesPersons(rows);
		linkCustomers(rows, salesPersonsByCode);
	}

	private Map<String, SalesPerson> upsertSalesPersons(List<SalesPersonResponse> rows) {
		Set<String> codes = rows.stream().map(SalesPersonResponse::getSalespersonCode).filter(Objects::nonNull)
				.map(this::normalizeCode).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		if (codes.isEmpty())
			return Map.of();

		List<SalesPerson> existing = salesPersonRepository.findAllBySalespersonCodeIn(codes);
		Map<String, SalesPerson> byCode = existing.stream()
				.collect(Collectors.toMap(SalesPerson::getSalespersonCode, sp -> sp));

		// distribuidores em lote (evita buscar por linha)
		Set<String> distributorIds = rows.stream().map(SalesPersonResponse::getDistributorAgentIdentifier)
				.filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		Map<String, Distributor> distributorById = distributorService.findAllByIdentifiers(distributorIds).stream()
				.collect(Collectors.toMap(Distributor::getIdentifier, d -> d));

		List<SalesPerson> toInsert = rows.stream().map(r -> normalizeCode(r.getSalespersonCode()))
				.filter(code -> !code.isBlank()).distinct().filter(code -> !byCode.containsKey(code)).map(code -> {
					SalesPersonResponse sample = rows.stream()
							.filter(r -> code.equals(normalizeCode(r.getSalespersonCode()))).findFirst().orElseThrow();

					Distributor distributor = distributorById.get(sample.getDistributorAgentIdentifier());
					return new SalesPerson(sample, distributor);
				}).toList();

		if (!toInsert.isEmpty()) {
			salesPersonRepository.saveAll(toInsert);
			toInsert.forEach(sp -> byCode.put(sp.getSalespersonCode(), sp));
		}

		return byCode;
	}

	private void linkCustomers(List<SalesPersonResponse> rows, Map<String, SalesPerson> byCode) {
		Set<String> customerIds = rows.stream().map(SalesPersonResponse::getCustomerIdentifier).filter(Objects::nonNull)
				.map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

		Map<String, Customer> customerById = customerService.findAllByIdentifiers(customerIds).stream()
				.collect(Collectors.toMap(Customer::getIdentifier, c -> c));

		for (SalesPersonResponse r : rows) {
			String code = normalizeCode(r.getSalespersonCode());
			if (code.isBlank())
				continue;

			SalesPerson sp = byCode.get(code);
			if (sp == null)
				continue;

			Customer customer = customerById.get(r.getCustomerIdentifier());
			if (customer == null)
				continue;

			sp.getCustomers().add(customer); // ideal ser Set<Customer>
		}

		salesPersonRepository.saveAll(byCode.values());
	}

	private String normalizeCode(String code) {
		if (code == null)
			return "";
		return code.trim();
	}

}
