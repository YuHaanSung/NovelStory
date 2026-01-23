package com.example.demo.dto;

public class CommentRequest {
    private String novelTitle;
    private int partNumber;
    private String authorId;
    private String content;

    // 기본 생성자가 반드시 있어야 스프링이 데이터를 채워줍니다.
    public CommentRequest() {}
    // Getter들 (스프링이 값을 읽을 때 씁니다)

    public String getNovelTitle() { return novelTitle; }
    public int getPartNumber() { return partNumber; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }


}
