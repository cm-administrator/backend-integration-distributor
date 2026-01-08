package com.br.distributors.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Distributor;
import com.br.distributors.models.RecentlyReadFiles;
import com.br.distributors.repository.RecentlyReadFilesRepository;
import com.br.distributors.request.BatchUpdateRequest;
import com.br.distributors.response.BatchUpdateResult;
import com.br.distributors.specification.RecentlyReadFilesSpecification;

@Service
public class RecentlyReadFilesService {

	private final RecentlyReadFilesRepository recentlyReadFilesRepository;
	private final DistributorService distributorService;

	public RecentlyReadFilesService(RecentlyReadFilesRepository repo, DistributorService distributorService) {
		this.recentlyReadFilesRepository = repo;
		this.distributorService = distributorService;
	}

	@Transactional
	public BatchUpdateResult updateIfNewerBatch(String distributorIdentifier, BatchUpdateRequest req) {
		if (req == null) {
			return BatchUpdateResult.none();
		}

		Distributor distributor = distributorService.getByIdentifier(distributorIdentifier);

		RecentlyReadFiles last = recentlyReadFilesRepository
				.findAll(RecentlyReadFilesSpecification.distributorIdentifierEquals(distributorIdentifier)).stream()
				.findFirst().orElseGet(() -> recentlyReadFilesRepository.save(new RecentlyReadFiles(distributor)));

		boolean updCustomers = shouldUpdate(req.customersInstant(), last.getCustomersInstant());
		boolean updProducts = shouldUpdate(req.productInstant(), last.getProductInstant());
		boolean updSalesPers = shouldUpdate(req.salesPersonInstant(), last.getSalesPersonInstant());
		boolean updStock = shouldUpdate(req.stockInstant(), last.getStockInstant());
		boolean updSales = shouldUpdate(req.salesInstant(), last.getSalesInstant());

		if (!(updCustomers || updProducts || updSalesPers || updStock || updSales)) {
			return new BatchUpdateResult(false, false, false, false, false);
		}

		RecentlyReadFiles next = cloneSnapshot(last, distributor);

		if (updCustomers) {
			next.setCustomersFile(req.customersFile());
			next.setCustomersInstant(req.customersInstant());
		}
		if (updProducts) {
			next.setProductFile(req.productFile());
			next.setProductInstant(req.productInstant());
		}
		if (updSalesPers) {
			next.setSalesPersonFile(req.salesPersonFile());
			next.setSalesPersonInstant(req.salesPersonInstant());
		}
		if (updStock) {
			next.setStockFile(req.stockFile());
			next.setStockInstant(req.stockInstant());
		}
		if (updSales) {
			next.setSalesFile(req.salesFile());
			next.setSalesInstant(req.salesInstant());
		}

		recentlyReadFilesRepository.save(next);

		return new BatchUpdateResult(updCustomers, updProducts, updSalesPers, updStock, updSales);
	}

	private boolean shouldUpdate(Instant instant, Instant lastInstant) {
		if (instant == null)
			return false;
		return lastInstant == null || instant.isAfter(lastInstant);
	}

	private RecentlyReadFiles cloneSnapshot(RecentlyReadFiles base, Distributor distributor) {
		RecentlyReadFiles next = new RecentlyReadFiles(distributor, base);
		return next;
	}

	@Transactional(readOnly = true)
	public Optional<RecentlyReadFiles> findBydistributorIdentifier(String distributorIdentifier) {
		return recentlyReadFilesRepository
				.findAll(RecentlyReadFilesSpecification.distributorIdentifierEquals(distributorIdentifier)).stream()
				.findFirst();
	}

}
