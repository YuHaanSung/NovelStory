package com.example.demo.domain;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private String novelTitle;
    private int partNumber;
    private String AuthorId;
    private String AuthorName;
    private String content;

    public Comment(Long id, String novelTitle, int partNumber, String authorId, String authorName, String content) {
        this.id = id;
        this.novelTitle = novelTitle;
        this.partNumber = partNumber;
        this.AuthorId = authorId;
        this.AuthorName = authorName;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(String authorId) {
        AuthorId = authorId;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
