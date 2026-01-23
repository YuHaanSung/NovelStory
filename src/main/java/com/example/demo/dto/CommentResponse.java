package com.example.demo.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private String content;
    private String authorName;
    private String authorId;

    public CommentResponse(Long id, String content, String authorName, String authorId) {
        this.id = id;
        this.content = content;
        this.authorName = authorName;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public String getAuthorName() {
        return authorName;
    }
    public String getAuthorId() {
        return authorId;
    }
}
