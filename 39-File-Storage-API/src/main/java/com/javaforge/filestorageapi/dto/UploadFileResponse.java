package com.javaforge.filestorageapi.dto;

import com.javaforge.filestorageapi.model.StoredFile;

import java.time.Instant;

public record UploadFileResponse(
        Long id,
        String fileName,
        String storedFileName,
        String contentType,
        Long size,
        String downloadUrl,
        Instant createdAt
) {
    public static UploadFileResponse from(StoredFile storedFile, String downloadBaseUrl) {
        return new UploadFileResponse(
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
