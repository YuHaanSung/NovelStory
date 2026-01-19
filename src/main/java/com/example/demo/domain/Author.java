package com.example.demo.domain;

import java.util.ArrayList;

//작가 클래스
public class Author {

    private String name; //작가이름
    private String introduction; //작가소개
    private String id;
    private String password;
    //해당 작가가 소유한 Novel들을 저장하는 배열

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }






    //생성자는 작가이름, 작가아이디, 비밀번호, 작가소개를 매개변수로 받음.
    public Author(String name, String introduction, String id, String password) {
        this.name = name;
        this.introduction = introduction;
        this.id = id;
        this.password = password;
    }




}
