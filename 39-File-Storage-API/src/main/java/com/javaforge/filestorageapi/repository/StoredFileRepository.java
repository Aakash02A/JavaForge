package com.javaforge.filestorageapi.repository;

import com.javaforge.filestorageapi.model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {

    Optional<StoredFile> findByStoredFileName(String storedFileName);
}
