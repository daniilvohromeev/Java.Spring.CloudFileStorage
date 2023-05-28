package com.main.cloudstorageservice.repository;

import com.main.cloudstorageservice.model.MinIOFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinIOFileRepository extends JpaRepository<MinIOFile, Long> {
}
