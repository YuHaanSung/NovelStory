package com.example.demo.domain;

import java.util.ArrayList;

//소설클래스
public class Novel {
    // 작품의 작가 Author타입 변수
    private String author;
    // 작품의 제목 String타입 변수
    private String title;
    // 작품의 태그 String타입 변수
    private ArrayList<String> tag = new ArrayList<>();

    // 작품의 요약 String타입 변수
    private String summary;
    //생성자는 작가, 제목, 태그, 요약을 매개변수로 받음.

    //해당 소설의 1편 2편과 같은 각편인 NovelPart들을 저장하는 배열
    ArrayList<NovelPart> novelParts = new ArrayList<>();

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
    public ArrayList<String> getTag() {
        return tag;
    }
    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    //생성자는 작가, 제목, 태그, 요약을 매개변수로 받음.
    public Novel(String author, String title, String tag, String summary) {
        this.author = author;
        this.title = title;
        this.tag.add(tag);
        this.summary = summary;
    }
    //addTag 메서드: 소설의 태그를 추가하거나 변경할 수 있음.
    public void addTag(String tag) {
        this.tag.add(tag);
    }


}
