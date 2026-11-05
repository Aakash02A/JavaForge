package com.javaforge.filestorageapi.service;

import com.javaforge.filestorageapi.exception.FileNotFoundException;
import com.javaforge.filestorageapi.exception.FileStorageException;
import com.javaforge.filestorageapi.model.StoredFile;
import com.javaforge.filestorageapi.repository.StoredFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    private final StoredFileRepository repository;
    private final Path uploadDirectory;
    private final String publicBaseUrl;

    public FileStorageService(StoredFileRepository repository,
                              @Value("${app.upload-dir:uploads}") String uploadDir,
                              @Value("${app.public-base-url:http://localhost:8080}") String publicBaseUrl) {
        this.repository = repository;
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        createUploadDirectory();
    }

    @Transactional
    public StoredFile upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is required");
        }

        String originalFileName = Objects.requireNonNullElse(file.getOriginalFilename(), "upload");
        String safeFileName = sanitizeFileName(originalFileName);
        String storedFileName = buildStoredFileName(safeFileName);

        try {
            Path targetFile = uploadDirectory.resolve(storedFileName).normalize();
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new FileStorageException("Could not save file: " + originalFileName, exception);
        }

        StoredFile storedFile = new StoredFile(
                safeFileName,
                storedFileName,
                file.getContentType() == null ? "application/octet-stream" : file.getContentType(),
                file.getSize(),
                Instant.now()
        );

        return repository.save(storedFile);
    }

    @Transactional(readOnly = true)
    public List<StoredFile> getAllFiles() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public StoredFile getFile(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));
    }

    @Transactional
    public void deleteFile(Long id) {
        StoredFile storedFile = repository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        try {
            Path targetFile = uploadDirectory.resolve(storedFile.getStoredFileName()).normalize();
            Files.deleteIfExists(targetFile);
        } catch (IOException exception) {
            throw new FileStorageException("Could not delete file: " + storedFile.getStoredFileName(), exception);
        }

        repository.delete(storedFile);
    }

    @Transactional(readOnly = true)
    public byte[] downloadFile(Long id) {
        StoredFile storedFile = getFile(id);
        Path targetFile = uploadDirectory.resolve(storedFile.getStoredFileName()).normalize();

        try {
            return Files.readAllBytes(targetFile);
        } catch (IOException exception) {
            throw new FileStorageException("Could not read file: " + storedFile.getStoredFileName(), exception);
        }
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    private String sanitizeFileName(String originalFileName) {
        String sanitized = originalFileName.replaceAll("\\s+", "_");
        sanitized = sanitized.replaceAll("[\\\\/:*?\"<>|]", "_");
        return sanitized.length() > 200 ? sanitized.substring(0, 200) : sanitized;
    }

    private String buildStoredFileName(String safeFileName) {
        String extension = "";
        int dotIndex = safeFileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < safeFileName.length() - 1) {
            extension = safeFileName.substring(dotIndex);
            safeFileName = safeFileName.substring(0, dotIndex);
        }

        String base = safeFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (base.isBlank()) {
            base = "upload";
        }

        return System.currentTimeMillis() + "-" + base + extension;
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new FileStorageException("Could not create upload directory", exception);
        }
    }
}
