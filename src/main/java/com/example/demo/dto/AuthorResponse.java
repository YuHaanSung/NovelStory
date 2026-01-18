package com.example.demo.dto;

import com.example.demo.domain.Novel;

import java.util.ArrayList;

public class AuthorResponse {
    private String authorId;
    private String name;
    private String introduction;
    private ArrayList<Novel> novels;

    public AuthorResponse(String authorId, String name, String introduction) {
        this.authorId = authorId;
        this.name = name;
        this.introduction = introduction;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }

    public String getIntroduction() {
        return introduction;
    }


    public ArrayList<Novel> getNovels() {
        return novels;
    }

    public void setNovels(ArrayList<Novel> novels) {
        this.novels = novels;
    }
}
