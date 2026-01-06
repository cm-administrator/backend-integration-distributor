package com.br.distributors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
