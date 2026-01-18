package com.example.demo.domain;

import java.util.ArrayList;

//작가 클래스
public class Author {

    private String name; //작가이름
    private String introduction; //작가소개
    private String id;
    private String password;
    private ArrayList<Novel> novels = new ArrayList<>();
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

    public ArrayList<Novel> getNovels() {
        return novels;
    }

    public void setNovels(ArrayList<Novel> novels) {
        this.novels = novels;
    }





    //생성자는 작가이름, 작가아이디, 비밀번호, 작가소개를 매개변수로 받음.
    public Author(String name, String introduction, String id, String password) {
        this.name = name;
        this.introduction = introduction;
        this.id = id;
        this.password = password;
    }






    //작가가 소설을 생성해서 소설목록에 추가함.
    public Novel createNovel(String title, String tag, String summary) {
        Novel newNovel = new Novel(this.name, title, tag, summary);
        this.novels.add(newNovel); // [중요] 내 소설 목록에 추가!
        return newNovel;
    }

    //오직 작가만이 자신만의 Novel의 일부인 NovelPart를 생성할 수 있음.
    public NovelPart createNovelPart(Novel novel, String partTitle, String content, int partNumber) {
        return new NovelPart(novel, partTitle, content, partNumber); //NovelPart 클래스의 생성
    }

    //오직 작가만이 Novel의 태그를 추가할 수 있음.
    public void addTagToNovel(Novel novel, String tag) {
        novel.addTag(tag); //Novel 클래스의 addTag 메서드 호출
    }


}
