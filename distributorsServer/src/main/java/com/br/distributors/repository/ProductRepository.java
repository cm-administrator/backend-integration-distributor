package com.br.distributors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
