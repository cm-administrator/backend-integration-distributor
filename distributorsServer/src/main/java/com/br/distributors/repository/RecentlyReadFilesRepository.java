package com.br.distributors.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.RecentlyReadFiles;

@Repository
public interface RecentlyReadFilesRepository extends JpaRepository<RecentlyReadFiles, Long> {
	Optional<RecentlyReadFiles> findByDistributor_Identifier(String distributorId);
}
