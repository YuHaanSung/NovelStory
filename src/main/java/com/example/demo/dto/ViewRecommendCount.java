package com.example.demo.dto;

public class ViewRecommendCount {
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
    public ViewRecommendCount(String novelTitle, String partTitle, String content, int partNumber, boolean isVip, int viewCount, int recommendCount) {
        this.novelTitle = novelTitle;
        this.partTitle = partTitle;
        this.content = content;
        this.partNumber = partNumber;
        this.isVip = isVip;
        this.viewCount = viewCount;
        this.recommendCount = recommendCount;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public String getPartTitle() {
        return partTitle;
    }

    public String getContent() {
        return content;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getRecommendCount() {
        return recommendCount;
    }
}
