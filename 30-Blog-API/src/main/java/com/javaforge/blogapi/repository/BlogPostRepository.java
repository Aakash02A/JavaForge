package com.javaforge.blogapi.repository;

import com.javaforge.blogapi.model.BlogPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    @Override
    @EntityGraph(attributePaths = {"author", "comments"})
    List<BlogPost> findAll();

    @Override
    @EntityGraph(attributePaths = {"author", "comments"})
    Optional<BlogPost> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"author", "comments"})
    BlogPost saveAndFlush(BlogPost post);
}
