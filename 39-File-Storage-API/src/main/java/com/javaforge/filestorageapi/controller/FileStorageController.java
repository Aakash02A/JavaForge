package com.javaforge.filestorageapi.controller;

import com.javaforge.filestorageapi.dto.StoredFileResponse;
import com.javaforge.filestorageapi.dto.UploadFileResponse;
import com.javaforge.filestorageapi.model.StoredFile;
import com.javaforge.filestorageapi.service.FileStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/files")
    public ResponseEntity<UploadFileResponse> upload(@RequestParam("file") MultipartFile file) {
        StoredFile saved = fileStorageService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UploadFileResponse.from(saved, fileStorageService.getPublicBaseUrl()));
    }

    @GetMapping("/files")
    public List<StoredFileResponse> listFiles() {
        return fileStorageService.getAllFiles().stream()
                .map(storedFile -> StoredFileResponse.from(storedFile, fileStorageService.getPublicBaseUrl()))
                .toList();
    }

    @GetMapping("/files/{id}")
    public StoredFileResponse getFile(@PathVariable Long id) {
        return StoredFileResponse.from(fileStorageService.getFile(id), fileStorageService.getPublicBaseUrl());
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        StoredFile storedFile = fileStorageService.getFile(id);
        byte[] bytes = fileStorageService.downloadFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storedFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(storedFile.getContentType()))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileStorageService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
