package com.br.distributors.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByIdentifier(String identifier);

	List<Customer> findAllByIdentifierIn(Collection<String> identifiers);

	boolean existsByIdentifier(String identifier);

	@Query("select c.identifier from Customer c where c.identifier in :identifiers")
	Set<String> findExistingIdentifiers(@Param("identifiers") Collection<String> identifiers);
}
