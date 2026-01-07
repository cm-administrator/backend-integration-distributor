package com.br.distributors.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Distributor;
import com.br.distributors.models.RecentlyReadFiles;
import com.br.distributors.repository.RecentlyReadFilesRepository;

@Service
public class RecentlyReadFilesService {

	private final RecentlyReadFilesRepository recentlyReadFilesRepository;
	private final DistributorService distributorService;

	public RecentlyReadFilesService(RecentlyReadFilesRepository recentlyReadFilesRepository,
			DistributorService distributorService) {
		this.recentlyReadFilesRepository = recentlyReadFilesRepository;
		this.distributorService = distributorService;
	}

	@Transactional
	public RecentlyReadFiles getOrCreateBydistributorIdentifierentifier(String distributorIdentifierentifier) {
		Distributor distributor = distributorService.getByIdentifier(distributorIdentifierentifier);

		return recentlyReadFilesRepository.findByDistributor_Identifier(distributorIdentifierentifier).orElseGet(() -> {
			RecentlyReadFiles r = new RecentlyReadFiles(distributor);

			return recentlyReadFilesRepository.save(r);
		});
	}

	/**
	 * Busca os últimos arquivos lidos (sem criar).
	 */
	@Transactional(readOnly = true)
	public Optional<RecentlyReadFiles> findBydistributorIdentifier(String distributorIdentifier) {
		return recentlyReadFilesRepository.findByDistributor_Identifier(distributorIdentifier);
	}

	@Transactional
	public boolean updateCustomersIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		RecentlyReadFiles r = getOrCreateBydistributorIdentifierentifier(distributorIdentifier);

		if (isNewer(fileInstant, r.getCustomersInstant())) {
			r.setCustomersFile(fileName);
			r.setCustomersInstant(fileInstant);
			recentlyReadFilesRepository.save(r);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean updateSalesIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		RecentlyReadFiles r = getOrCreateBydistributorIdentifierentifier(distributorIdentifier);

		if (isNewer(fileInstant, r.getSalesInstant())) {
			r.setSalesFile(fileName);
			r.setSalesInstant(fileInstant);
			recentlyReadFilesRepository.save(r);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean updateStockIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		RecentlyReadFiles r = getOrCreateBydistributorIdentifierentifier(distributorIdentifier);

		if (isNewer(fileInstant, r.getStockInstant())) {
			r.setStockFile(fileName);
			r.setStockInstant(fileInstant);
			recentlyReadFilesRepository.save(r);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean updateProductsIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		RecentlyReadFiles r = getOrCreateBydistributorIdentifierentifier(distributorIdentifier);

		if (isNewer(fileInstant, r.getProductInstant())) {
			r.setProductFile(fileName);
			r.setProductInstant(fileInstant);
			recentlyReadFilesRepository.save(r);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean updateSalesPersonIfNewer(String distributorIdentifier, String fileName, Instant fileInstant) {
		RecentlyReadFiles r = getOrCreateBydistributorIdentifierentifier(distributorIdentifier);

		if (isNewer(fileInstant, r.getSalesPersonInstant())) {
			r.setSalesPersonFile(fileName);
			r.setSalesPersonInstant(fileInstant);
			recentlyReadFilesRepository.save(r);
			return true;
		}
		return false;
	}

	private boolean isNewer(Instant candidate, Instant last) {
		if (candidate == null)
			return false;
		if (last == null)
			return true;
		return candidate.isAfter(last);
	}
}
