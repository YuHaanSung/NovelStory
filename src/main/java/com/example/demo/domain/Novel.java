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

    private int totalLikes; // 소설의 총 좋아요 수
    private int totalCounts; // 소설의 총 조회수

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

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    //생성자는 작가, 제목, 태그, 요약을 매개변수로 받음.
    public Novel(String author, String title, String tag, String summary) {
        this.author = author;
        this.title = title;
        this.summary = summary;
        // [수정] 쉼표로 쪼개서 넣기 (빈칸도 제거)
        if (tag != null && !tag.isEmpty()) {
            String[] splitTags = tag.split(",");
            for (String t : splitTags) {
                this.tag.add(t.trim());
            }
        }
        this.totalCounts = 0;
        this.totalLikes = 0;
    }

    //addTag 메서드: 소설의 태그를 추가하거나 변경할 수 있음.
    public void addTag(String tag) {
        this.tag.add(tag);
    }


}
