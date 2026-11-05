package com.javaforge.blogapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "blog_comments")
public class BlogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String commenterName;

    @Column(nullable = false, length = 4000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private BlogPost post;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected BlogComment() {
    }

    public BlogComment(String commenterName, String content, BlogPost post) {
        this.commenterName = commenterName;
        this.content = content;
        this.post = post;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void detachFromPost() {
        post = null;
    }
}
