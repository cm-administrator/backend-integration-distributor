package com.br.distributors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.SalesPerson;

@Repository
public interface SalesPersonRepository extends JpaRepository<SalesPerson, Long> {
}
