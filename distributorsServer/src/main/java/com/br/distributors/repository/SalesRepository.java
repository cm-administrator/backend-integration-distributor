package com.br.distributors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
}
