package com.example.demo.domain;

public class NovelPart {
    //부모소설의 이름
    private String novelTitle;
    //각편의 제목
    private String partTitle;
    //각편의 내용
    private String content;
    //각편의 번호
    private int partNumber;

    //생성자는 부모소설, 각편의 제목, 내용, 번호를 매개변수로 받음.
    public NovelPart(Novel novel, String partTitle, String content, int partNumber) {
        this.novelTitle = novel.getTitle();
        this.partTitle = partTitle;
        this.content = content;
        this.partNumber = partNumber;
    }
}
