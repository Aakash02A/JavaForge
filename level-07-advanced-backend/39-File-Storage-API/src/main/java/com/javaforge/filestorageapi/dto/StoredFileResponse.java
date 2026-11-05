package com.javaforge.filestorageapi.dto;

import com.javaforge.filestorageapi.model.StoredFile;

import java.time.Instant;

public record StoredFileResponse(
        Long id,
        String fileName,
        String storedFileName,
        String contentType,
        Long size,
        String downloadUrl,
        Instant createdAt
) {
    public static StoredFileResponse from(StoredFile storedFile, String downloadBaseUrl) {
        return new StoredFileResponse(
                storedFile.getId(),
                storedFile.getFileName(),
                storedFile.getStoredFileName(),
                storedFile.getContentType(),
                storedFile.getSize(),
                downloadBaseUrl + "/api/files/" + storedFile.getId() + "/download",
                storedFile.getCreatedAt()
        );
    }
}
