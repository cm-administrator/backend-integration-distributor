package com.br.distributors.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.SalesPerson;

@Repository
public interface SalesPersonRepository extends JpaRepository<SalesPerson, Long> {

	Optional<SalesPerson> findBySalespersonCode(String normalizeCode);

	boolean existsBySalespersonCode(String normalizeCode);

	@Query("select sp.salespersonCode from SalesPerson sp where sp.salespersonCode in :codes")
	Set<String> findExistingCodes(Set<String> codes);

	List<SalesPerson> findAllBySalespersonCodeIn(Set<String> codes);
}
