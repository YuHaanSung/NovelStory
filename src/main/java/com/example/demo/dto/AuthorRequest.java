package com.example.demo.dto;

public class AuthorRequest {
    private String name;
    private String introduction;
    private String id;
    private String password;

    // 기본 생성자가 반드시 있어야 스프링이 데이터를 채워줍니다.
    public AuthorRequest() {}

    // Getter들 (스프링이 값을 읽을 때 씁니다)
    public String getName() { return name; }
    public String getIntroduction() { return introduction; }
    public String getId() { return id; }
    public String getPassword() { return password; }
}