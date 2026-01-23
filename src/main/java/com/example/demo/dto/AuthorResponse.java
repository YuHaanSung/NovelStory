package com.example.demo.dto;

import com.example.demo.domain.Novel;
import com.example.demo.domain.Role;

import java.util.ArrayList;

public class AuthorResponse {
    private String authorId;
    private String name;
    private String introduction;
    private ArrayList<Novel> novels;
    private Role role;

    public AuthorResponse(String authorId, String name, String introduction, Role role) {
        this.authorId = authorId;
        this.name = name;
        this.introduction = introduction;
        this.role = role;

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

    public Role getRole() {
        return role;
    }

}
