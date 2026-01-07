package com.br.distributors.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Customer;
import com.br.distributors.models.Distributor;
import com.br.distributors.models.Product;
import com.br.distributors.models.Sales;
import com.br.distributors.models.SalesPerson;
import com.br.distributors.repository.SalesRepository;
import com.br.distributors.request.SalesFileResponse;
import com.br.distributors.request.SalesResponse;

@Service
public class SalesService {

	private final SalesRepository salesRepository;
	private final CustomerService customerService;
	private final ProductService productService;
	private final DistributorService distributorService;
	private final SalesPersonService salesPersonService;

	public SalesService(SalesRepository salesRepository, CustomerService customerService, ProductService productService,
			DistributorService distributorService, SalesPersonService salesPersonService) {
		this.salesRepository = salesRepository;
		this.customerService = customerService;
		this.productService = productService;
		this.distributorService = distributorService;
		this.salesPersonService = salesPersonService;
	}

	/**
	 * Insere apenas vendas ainda inexistentes (por identifier). Evita N queries
	 * (uma por item) buscando todos os identifiers existentes de uma vez.
	 */
	@Transactional
	public void saveAll(SalesFileResponse file) {
		List<Sales> sales = file.getItems().stream().map(this::toSaleOrNull) // já filtra inválidos (ex.: produto null)
				.filter(java.util.Objects::nonNull)
				.filter(sale -> !salesRepository
						.existsByCustomer_IdentifierAndDistributor_IdentifierAndProduct_BarcodeAndTransactionDateAndSequence(
								sale.getCustomer().getIdentifier(), sale.getDistributor().getIdentifier(),
								sale.getProduct().getBarcode(), sale.getTransactionDate(), sale.getSequence()))
				.toList();

		if (!sales.isEmpty()) {
			salesRepository.saveAll(sales);
		}
	}

	private Sales toSaleOrNull(SalesResponse response) {
		Product product = productService.getByBarcode(response.getProductBarcode());
		if (product == null) {
			return null;
		} else {
			Customer customer = customerService.getByIdentifier(response.getCustomerIdentifier());
			Distributor distributor = distributorService.getByIdentifier(response.getDistributorIdentifier());
			SalesPerson salesPerson = salesPersonService.getBySalespersonCode(response.getSalespersonCode());
			Sales sale = new Sales(response, customer, product, salesPerson, distributor);
			return sale;
		}
	}

}
