package com.example.demo.repository;

import com.example.demo.domain.*;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataStore {
    // AuthorController에 있던 리스트들을 모두 이쪽으로 옮깁니다
    public List<Author> authorList = new ArrayList<>();
    public List<Novel> novelList = new ArrayList<>();
    public List<NovelPart> novelPartList = new ArrayList<>();
    public List<Comment> commentList = new ArrayList<>();
    public Long commentIdCounter = 1L;
}