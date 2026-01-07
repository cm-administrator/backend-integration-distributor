package com.br.distributors.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.distributors.models.Supplier;
import com.br.distributors.repository.SupplierRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SupplierService {

	private final SupplierRepository supplierRepository;

	public SupplierService(SupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

	@Transactional(readOnly = true)
	public Supplier findById(Long supplierId) {
		return supplierRepository.findById(supplierId)
				.orElseThrow(() -> new EntityNotFoundException("Distribuidor não encontrado para ID: " + supplierId));
	}

	@Transactional(readOnly = true)
	public Optional<Supplier> findByIdentifier(String identifier) {
		return supplierRepository.findByIdentifier(identifier);
	}

	@Transactional
	public Supplier save(String identifier, String legalName) {
		Optional<Supplier> existing = findByIdentifier(identifier);
		if (existing.isEmpty()) {
			return supplierRepository.save(new Supplier(identifier, legalName));
		}
		return existing.get();
	}

	public Supplier getOrCreate(String identifier, String legalName) {
		return findByIdentifier(identifier).orElseGet(() -> save(identifier, legalName));
	}
}
