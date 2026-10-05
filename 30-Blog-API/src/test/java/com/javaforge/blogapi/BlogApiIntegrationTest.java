package com.javaforge.blogapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.blogapi.repository.AuthorRepository;
import com.javaforge.blogapi.repository.BlogPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BlogPostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        postRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void persistsAuthorPostAndCommentsAcrossJpaRelationships() throws Exception {
        long authorId = createAuthor("writer@example.com");
        MvcResult createdPost = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequest(authorId, "JPA Relationships")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesRegex(
                        "http://localhost/api/posts/\\d+")))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value("Writer"))
                .andReturn();
        long postId = objectMapper.readTree(createdPost.getResponse().getContentAsString())
                .get("id").asLong();

        MvcResult createdComment = mockMvc.perform(post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"commenterName":"Reader","content":"This helped me understand JPA."}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commenterName").value("Reader"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesRegex(
                        "http://localhost/api/posts/" + postId + "/comments/\\d+")))
                .andReturn();
        long commentId = objectMapper.readTree(createdComment.getResponse().getContentAsString())
                .get("id").asLong();
        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId));

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.comments[0].content").value("This helped me understand JPA."));
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postCount").value(1));

        mockMvc.perform(put("/api/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequest(authorId, "Updated Relationships")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Relationships"))
                .andExpect(jsonPath("$.comments.length()").value(1));

        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNoContent());
        assertEquals(0, postRepository.count());
    }

    @Test
    void rejectsPostWhenReferencedAuthorDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequest(99999, "Orphan post")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 99999 was not found"));
    }

    @Test
    void validatesAuthorAndPostRequests() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","email":"not-an-email","bio":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").value("Email must be valid"));

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","content":" ","authorId":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists())
                .andExpect(jsonPath("$.validationErrors.content").exists())
                .andExpect(jsonPath("$.validationErrors.authorId").value("Author ID must be positive"));
    }

    private long createAuthor(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Writer","email":"%s","bio":"Writes about software"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode author = objectMapper.readTree(result.getResponse().getContentAsString());
        return author.get("id").asLong();
    }

    private String postRequest(long authorId, String title) {
        return """
                {"title":"%s","content":"A detailed article about entity relationships.",
                "authorId":%d}
                """.formatted(title, authorId);
    }
}
