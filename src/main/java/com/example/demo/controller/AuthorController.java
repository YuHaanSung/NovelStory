package com.example.demo.controller;

import com.example.demo.domain.Author;
import com.example.demo.domain.Novel;
import com.example.demo.dto.AuthorLoginRequest;
import com.example.demo.dto.AuthorNewNovelResponse;
import com.example.demo.dto.AuthorRequest;
import com.example.demo.dto.AuthorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthorController {

    // 임시 저장소 (DB 대신 리스트에 보관)
    private List<Author> authorList = new ArrayList<>();


    @PostMapping("/api/signup") // 프론트엔드가 이 주소로 데이터를 보냅니다.
    public String register(@RequestBody AuthorRequest request) {

        // 1. 님이 만든 Author 클래스의 생성자로 객체 생성
        Author newAuthor = new Author(
                request.getName(),
                request.getIntroduction(),
                request.getId(),
                request.getPassword()
        );

        // 2. 리스트에 저장
        authorList.add(newAuthor);

        return newAuthor.getName() + " 작가님, 회원가입이 완료되었습니다!";
    }

    @PostMapping("/api/login")
    public String login(@RequestBody AuthorLoginRequest request) { // 로그인 전용 DTO 사용

        // 리스트에서 아이디랑 비번 맞는 작가 찾기 (자바 스트림 문법 활용)
        for (Author author : authorList) {
            if (author.getId().equals(request.getId()) &&
                    author.getPassword().equals(request.getPassword())) {
                return author.getName() + " 작가님, 로그인 성공!";
            }
        }

        return "로그인 실패: 아이디나 비밀번호를 확인하세요.";
    }

    @GetMapping("/api/authors/{id}")
    public AuthorResponse getAuthorInfo(@PathVariable String id) {
        // 리스트에서 아이디에 맞는 작가 찾기
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                // AuthorResponse DTO로 변환하여 반환
                AuthorResponse response = new AuthorResponse(
                        author.getId(),
                        author.getName(),
                        author.getIntroduction()
                );
                response.setNovels(author.getNovels());
                return response;
            }
        }
        return null; // 작가를 찾지 못한 경우 null 반환 (실제 구현에서는 예외 처리 필요)
    }


    @PostMapping("/api/authors/{id}/NewNovelRegister")
        public String NovelRegister(@PathVariable String id, @RequestBody AuthorNewNovelResponse request){
            // 리스트에서 아이디에 맞는 작가 찾기
            for (Author author : authorList) {
                if (author.getId().equals(id)) {
                    // 새로운 소설 생성 및 작가의 소설 목록에 추가
                    Author.createNovel(id, request.getTitle(), request.getTag(), request.getSummary());
                    return "작가 " + author.getName() + "님의 새로운 소설 '" + request.getNovel().getTitle() + "'이(가) 등록되었습니다!";
                }
            }
            return "작가를 찾을 수 없습니다."; // 작가를 찾지 못한 경우


    }


}