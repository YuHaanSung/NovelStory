package com.example.demo.domain;

import java.util.ArrayList;

//소설클래스
public class Novel {
    // 작품의 작가 Author타입 변수
    private String author;
    // 작품의 제목 String타입 변수
    private String title;

    // 작품의 요약 String타입 변수
    private String summary;
    //생성자는 작가, 제목, 태그, 요약을 매개변수로 받음.

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    //생성자는 작가, 제목, 태그, 요약을 매개변수로 받음.
    public Novel(String author, String title, String summary) {
        this.author = author;
        this.title = title;
        this.summary = summary;
    }

}
