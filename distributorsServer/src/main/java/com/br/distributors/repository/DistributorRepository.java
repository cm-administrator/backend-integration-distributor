package com.br.distributors.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Distributor;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long> {

	Optional<Distributor> findByIdentifier(String identifier);

	List<Distributor> findAllByIdentifierIn(Collection<String> identifiers);

	boolean existsByIdentifier(String identifier);

}
