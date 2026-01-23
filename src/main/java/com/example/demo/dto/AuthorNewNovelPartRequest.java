package com.example.demo.dto;

public class AuthorNewNovelPartRequest {

    //부모소설의 이름
    private String novelTitle;
    //각편의 제목
    private String partTitle;
    //각편의 내용
    private String content;
    //각편의 번호
    private int partNumber;

    //vip 소설인지 여부
    private boolean isVip;

    // 기본 생성자가 반드시 있어야 스프링이 데이터를 채워줍니다.
    public AuthorNewNovelPartRequest() {}

    // Getter들 (스프링이 값을 읽을 때 씁니다)
    public String getNovelTitle() { return novelTitle; }
    public String getPartTitle() { return partTitle; }
    public String getContent() { return content; }
    public int getPartNumber() { return partNumber; }
    public boolean isVip() { return isVip; }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }

    public void setPartTitle(String partTitle) {
        this.partTitle = partTitle;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public void setVip(boolean vip) {
        this.isVip = vip;
    }
}
