package com.example.demo.controller;

import com.example.demo.domain.Author;
import com.example.demo.domain.Novel;
import com.example.demo.domain.NovelPart;
import com.example.demo.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthorController {

    // 임시 저장소 (DB 대신 리스트에 보관)
    private List<Author> authorList = new ArrayList<>();

    private List<Novel> novelList = new ArrayList<>();

    private List<NovelPart> NovelPartList = new ArrayList<>();

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

    // 작가 정보 조회 기능
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
                ArrayList<Novel> myNovels = new ArrayList<>();
                for (Novel novel : novelList) {
                    // 소설에 적힌 작가 이름(Author)이 내 이름(foundAuthor.getName())과 같으면?
                    if (novel.getAuthor().equals(author.getName())) {
                        myNovels.add(novel); // 내 소설 목록에 추가!
                    }
                }
                response.setNovels(myNovels);
                return response;
            }
        }
        return null; // 작가를 찾지 못한 경우 null 반환 (실제 구현에서는 예외 처리 필요)
    }

    // 작가의 새로운 소설 등록 기능
    @PostMapping("/api/authors/{id}/NewNovelRegister")
    public String NovelRegister(@PathVariable String id, @RequestBody AuthorNewNovelRequest request){
        // 리스트에서 아이디에 맞는 작가 찾기
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                // Novel 객체 생성
                Novel newNovel = new Novel(
                        author.getName(),
                        request.getTitle(),
                        request.getTag(),
                        request.getSummary()
                );

                // 소설 리스트에 저장
                novelList.add(newNovel);

                return "작가 " + author.getName() + "님의 새로운 소설 '" + newNovel.getTitle() + "'이(가) 등록되었습니다!";
            }
        }
        return "소설 등록 실패: 작가를 찾을 수 없습니다.";

    }

    // 작가의 새로운 소설 회차(파트) 등록 기능
    @PostMapping("/api/authors/{id}/NewNovelPartRegister")
    public String NovelPartRegister(@PathVariable String id, @RequestBody AuthorNewNovelPartRequest request){
        // 리스트에서 아이디에 맞는 작가 찾기
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                // NovelPart 객체 생성
                NovelPart newNovelPart = new NovelPart(
                        request.getNovelTitle(),
                        request.getPartTitle(),
                        request.getContent(),
                        request.getPartNumber()
                );

                // 소설 파트 리스트에 저장
                NovelPartList.add(newNovelPart);

                return "작가 " + author.getName() + "님의 소설 '" + newNovelPart.getNovelTitle() + "'에 새로운 파트 '" + newNovelPart.getPartTitle() + "'이(가) 등록되었습니다!";
            }
        }
        return "소설 파트 등록 실패: 작가를 찾을 수 없습니다.";
    }

    // 1. [독자용] 소설 제목으로 '소설 정보' 가져오기
    @GetMapping("/api/novels/{novelTitle}")
    public Novel getNovelByTitle(@PathVariable String novelTitle) {
        for (Novel novel : novelList) {
            if (novel.getTitle().equals(novelTitle)) {
                return novel;
            }
        }
        return null;
    }
    // 1. [독자용] 소설 제목으로 '전체 파트 목록' 가져오기
    @GetMapping("/api/novels/{novelTitle}/parts")
    public List<NovelPart> getNovelParts(@PathVariable String novelTitle) {
        List<NovelPart> result = new ArrayList<>();

        // 전체 파트 보관소(NovelPartList)를 뒤져서 제목이 같은 것만 찾아냅니다.
        for (NovelPart part : NovelPartList) {
            if (part.getNovelTitle().equals(novelTitle)) {
                result.add(part);
            }
        }
        return result;
    }


    // 2. [독자용] 소설 제목과 회차 번호로 '글 내용' 가져오기
    @GetMapping("/api/novels/{novelTitle}/parts/{partNumber}")
    public NovelPart getNovelPart(@PathVariable String novelTitle, @PathVariable int partNumber) {
        for (NovelPart part : NovelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                return part;
            }
        }
        return null;
    }

    //소설관리페이지로 이동해서 작가가 자신의 소설들을 확인할 수 있도록 함.
    @GetMapping("/api/authors/{id}/manageNovels")
    public List<Novel> manageNovels(@PathVariable String id) {
        // 리스트에서 아이디에 맞는 작가 찾기
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                ArrayList<Novel> myNovels = new ArrayList<>();
                for (Novel novel : novelList) {
                    // 소설에 적힌 작가 이름(Author)이 내 이름(foundAuthor.getName())과 같으면?
                    if (novel.getAuthor().equals(author.getName())) {
                        myNovels.add(novel); // 내 소설 목록에 추가!
                    }
                }
                return myNovels;
            }
        }
        return new ArrayList<>(); // 작가를 찾지 못한 경우 빈 리스트 반환
    }

    //해당 소설의 정보를 띄워주고 수정할수있음
    @GetMapping("/api/authors/{id}/manageNovels/{novelTitle}")
    public Novel manageSpecificNovel(@PathVariable String id, @PathVariable String novelTitle) {
        // 리스트에서 아이디에 맞는 작가 찾기
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                for (Novel novel : novelList) {
                    // 소설에 적힌 작가 이름(Author)이 내 이름(foundAuthor.getName())과 같고, 소설 제목이 일치하면?
                    if (novel.getAuthor().equals(author.getName()) && novel.getTitle().equals(novelTitle)) {
                        return novel; // 해당 소설 반환
                    }
                }
            }
        }
        return null; // 작가나 소설을 찾지 못한 경우 null 반환
    }

    //위의 코드로 현재 소설 정보를 보여줬으니 이제 수정된 정보를 받아서 반영하는 코드를 작성해야 합니다.
    @PutMapping("/api/authors/{id}/manageNovels/{novelTitle}")
    public String updateSpecificNovel(@PathVariable String id, @PathVariable String novelTitle, @RequestBody AuthorNewNovelRequest request) {
        // 리스트에서 아이디에 맞는 작가 찾기
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                for (Novel novel : novelList) {
                    // 소설에 적힌 작가 이(Author)이 내 이름(foundAuthor.getName())과 같고, 소설 제목이 일치하면?
                    if (novel.getAuthor().equals(author.getName()) && novel.getTitle().equals(novelTitle)) {
                        // 소설 정보 업데이트
                        novel.setTitle(request.getTitle());
                        novel.setSummary(request.getSummary());
                        ArrayList<String> newTags = new ArrayList<>();
                        if (request.getTag() != null && !request.getTag().isEmpty()) {
                            String[] splitTags = request.getTag().split(",");
                            for (String t : splitTags) {
                                newTags.add(t.trim());
                            }
                        }
                        novel.setTag(newTags);
                        return "소설 '" + novelTitle + "'이(가) 성공적으로 업데이트되었습니다!";
                    }
                }
            }
        }
        return "소설 업데이트 실패: 작가나 소설을 찾을 수 없습니다.";
    }

}