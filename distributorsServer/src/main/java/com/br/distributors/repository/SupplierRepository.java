package com.br.distributors.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

	Optional<Supplier> findByIdentifier(String identifier);

}
