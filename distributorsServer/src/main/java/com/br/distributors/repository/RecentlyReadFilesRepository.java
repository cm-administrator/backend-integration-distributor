package com.br.distributors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.br.distributors.models.RecentlyReadFiles;

@Repository
public interface RecentlyReadFilesRepository
		extends JpaRepository<RecentlyReadFiles, Long>, JpaSpecificationExecutor<RecentlyReadFiles> {
}
