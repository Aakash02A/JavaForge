package com.javaforge.blogapi.service;

import com.javaforge.blogapi.dto.AuthorResponse;
import com.javaforge.blogapi.dto.CommentResponse;
import com.javaforge.blogapi.dto.CreateAuthorRequest;
import com.javaforge.blogapi.dto.CreateCommentRequest;
import com.javaforge.blogapi.dto.CreatePostRequest;
import com.javaforge.blogapi.dto.PostResponse;
import com.javaforge.blogapi.dto.UpdatePostRequest;
import com.javaforge.blogapi.exception.AuthorNotFoundException;
import com.javaforge.blogapi.exception.CommentNotFoundException;
import com.javaforge.blogapi.exception.DuplicateAuthorEmailException;
import com.javaforge.blogapi.exception.PostNotFoundException;
import com.javaforge.blogapi.model.Author;
import com.javaforge.blogapi.model.BlogComment;
import com.javaforge.blogapi.model.BlogPost;
import com.javaforge.blogapi.repository.AuthorRepository;
import com.javaforge.blogapi.repository.BlogPostRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class BlogService {
    private final AuthorRepository authorRepository;
    private final BlogPostRepository postRepository;

    public BlogService(AuthorRepository authorRepository, BlogPostRepository postRepository) {
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
    }

    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        String email = normalizeEmail(request.email());
        if (authorRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateAuthorEmailException(email);
        }
        Author author = new Author(request.name().trim(), email, normalizeBio(request.bio()));
        return AuthorResponse.from(authorRepository.save(author));
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAllAuthors() {
        return authorRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(AuthorResponse::from)
                .toList();
    }

    public PostResponse createPost(CreatePostRequest request) {
        Author author = getAuthor(request.authorId());
        BlogPost post = new BlogPost(request.title().trim(), request.content().trim(), author);
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(PostResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse findPostById(long id) {
        return PostResponse.from(getPost(id));
    }

    public PostResponse updatePost(long id, UpdatePostRequest request) {
        BlogPost post = getPost(id);
        Author author = getAuthor(request.authorId());
        post.updateDetails(request.title().trim(), request.content().trim(), author);
        return PostResponse.from(postRepository.save(post));
    }

    public CommentResponse addComment(long postId, CreateCommentRequest request) {
        BlogPost post = getPost(postId);
        post.addComment(request.commenterName().trim(), request.content().trim());
        BlogPost savedPost = postRepository.saveAndFlush(post);
        BlogComment comment = savedPost.getComments().getLast();
        return CommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public CommentResponse findComment(long postId, long commentId) {
        return getPost(postId).getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .map(CommentResponse::from)
                .orElseThrow(() -> new CommentNotFoundException(commentId, postId));
    }

    public void deletePost(long id) {
        BlogPost post = getPost(id);
        postRepository.delete(post);
    }

    private Author getAuthor(long id) {
        return authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
    }

    private BlogPost getPost(long id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeBio(String bio) {
        return bio == null ? "" : bio.trim();
    }
}
