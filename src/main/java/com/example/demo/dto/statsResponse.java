package com.example.demo.dto;

public class statsResponse {
    private String title;
    // 작품의 요약 String타입 변수

    private int totalLikes; // 소설의 총 좋아요 수
    private int totalCounts; // 소설의 총 조회수

    public statsResponse(String title, int totalLikes, int totalCounts) {
        this.title = title;
        this.totalLikes = totalLikes;
        this.totalCounts = totalCounts;
    }
    public String getTitle() {
        return title;
    }
    public int getTotalLikes() {
        return totalLikes;
    }
    public int getTotalCounts() {
        return totalCounts;
    }



}

