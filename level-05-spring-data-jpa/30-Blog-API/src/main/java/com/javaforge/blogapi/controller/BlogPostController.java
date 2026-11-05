package com.javaforge.blogapi.controller;

import com.javaforge.blogapi.dto.CommentResponse;
import com.javaforge.blogapi.dto.CreateCommentRequest;
import com.javaforge.blogapi.dto.CreatePostRequest;
import com.javaforge.blogapi.dto.PostResponse;
import com.javaforge.blogapi.dto.UpdatePostRequest;
import com.javaforge.blogapi.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {
    private final BlogService service;

    public BlogPostController(BlogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody CreatePostRequest request) {
        PostResponse created = service.createPost(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<PostResponse> findAll() {
        return service.findAllPosts();
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable long id) {
        return service.findPostById(id);
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable long id, @Valid @RequestBody UpdatePostRequest request) {
        return service.updatePost(id, request);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable long id,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse created = service.addComment(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{commentId}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}/comments/{commentId}")
    public CommentResponse findComment(@PathVariable long id, @PathVariable long commentId) {
        return service.findComment(id, commentId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
