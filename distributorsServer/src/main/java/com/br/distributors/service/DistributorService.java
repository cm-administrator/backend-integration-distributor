package com.br.distributors.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Distributor;
import com.br.distributors.repository.DistributorRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DistributorService {

	private final DistributorRepository distributorRepository;

	public DistributorService(DistributorRepository distributorRepository) {
		this.distributorRepository = distributorRepository;
	}

	@Transactional(readOnly = true)
	public Distributor findById(Long distributorId) {
		return distributorRepository.findById(distributorId).orElseThrow(
				() -> new EntityNotFoundException("Distribuidor não encontrado para ID: " + distributorId));
	}

	@Transactional(readOnly = true)
	public List<Distributor> findAllByIdentifiers(Set<String> identifiers) {
		return distributorRepository.findAllByIdentifierIn(identifiers);

	}

	@Transactional(readOnly = true)
	private Optional<Distributor> findByIdentifier(String identifier) {
		return distributorRepository.findByIdentifier(identifier);
	}

	@Transactional(readOnly = true)
	public Distributor getByIdentifier(String identifier) {
		return findByIdentifier(identifier).orElseThrow(
				() -> new EntityNotFoundException("Distribuidor não encontrado para identificador: " + identifier));
	}

	@Transactional
	private Distributor save(String identifier, String legalName) {
		Optional<Distributor> existing = findByIdentifier(identifier);
		if (existing.isEmpty()) {
			return distributorRepository.save(new Distributor(identifier, legalName));
		}
		return existing.get();
	}

	public Distributor getOrCreate(String identifier, String legalName) {
		return findByIdentifier(identifier).orElseGet(() -> save(identifier, legalName));
	}
}
