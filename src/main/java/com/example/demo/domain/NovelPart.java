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

    //vip 소설인지 여부
    private boolean isVip;

    //이 소설편의 조회수
    private int viewCount;

    // 이 소설편의 추천수
    private int recommendCount;


    //생성자는 부모소설, 각편의 제목, 내용, 번호를 매개변수로 받음.
    public NovelPart(String novel, String partTitle, String content, int partNumber) {
        this.novelTitle = novel;
        this.partTitle = partTitle;
        this.content = content;
        this.partNumber = partNumber;
        this.isVip = false;
        this.viewCount = 0;
        this.recommendCount = 0;
    }

    //Getter들
    public String getNovelTitle() { return novelTitle; }
    public String getPartTitle() { return partTitle; }
    public String getContent() { return content; }
    public int getPartNumber() { return partNumber; }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }
    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getRecommendCount() {
        return recommendCount;
    }
    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }
}
