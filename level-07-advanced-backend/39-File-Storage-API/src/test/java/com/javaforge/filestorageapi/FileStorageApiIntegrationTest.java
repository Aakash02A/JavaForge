package com.javaforge.filestorageapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileStorageApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.javaforge.filestorageapi.repository.StoredFileRepository storedFileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() {
        storedFileRepository.deleteAll();
    }

    @Test
    void uploadListAndDownloadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                "hello world".getBytes()
        );

        String uploadResponse = mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("notes.txt"))
                .andExpect(jsonPath("$.contentType").value("text/plain"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(uploadResponse)
                .get("id")
                .asLong();

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("notes.txt"));

        mockMvc.perform(get("/api/files/{id}/download", id))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray())
                        .isEqualTo("hello world".getBytes()));
    }

    @Test
    void deleteFileRemovesStoredRecordAndPhysicalFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                "pdf-data".getBytes()
        );

        String uploadResponse = mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(uploadResponse)
                .get("id")
                .asLong();

        mockMvc.perform(delete("/api/files/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/files/{id}", id))
                .andExpect(status().isNotFound());
    }
}
