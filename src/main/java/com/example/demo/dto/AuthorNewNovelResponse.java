package com.example.demo.dto;

import com.example.demo.domain.Novel;

import java.util.ArrayList;

public class AuthorNewNovelResponse {
    private String title;
    private String summary;
    private String tag;

    public AuthorNewNovelResponse(){}

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getTag() {
        return tag;
    }

}
