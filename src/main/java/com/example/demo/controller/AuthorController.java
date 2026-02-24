package com.example.demo.controller;

import com.example.demo.domain.*;
import com.example.demo.dto.*;
import com.example.demo.repository.DataStore; // [추가] 데이터 저장소
import com.example.demo.service.FileService; // [추가] 파일 저장 기능
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthorController {
    @Value("${portone.api.secret}")
    private String apiSecret;

    // [변경] 기존 리스트들을 제거하고 DataStore와 FileService를 사용합니다.
    private final DataStore dataStore;
    private final FileService fileService;

    // [변경] 생성자 주입
    public AuthorController(DataStore dataStore, FileService fileService) {
        this.dataStore = dataStore;
        this.fileService = fileService;
    }

    @PostMapping("/api/signup")
    public String register(@RequestBody AuthorRequest request) {
        Author newAuthor = new Author(
                request.getName(),
                request.getIntroduction(),
                request.getId(),
                request.getPassword()
        );

        dataStore.authorList.add(newAuthor); // 저장소에 추가
        fileService.saveData(); // 💾 파일 저장

        return newAuthor.getName() + " 작가님, 회원가입이 완료되었습니다!";
    }

    @PostMapping("/api/login")
    public String login(@RequestBody AuthorLoginRequest request) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(request.getId()) &&
                    author.getPassword().equals(request.getPassword())) {
                return author.getName() + " 작가님, 로그인 성공!";
            }
        }
        return "로그인 실패: 아이디나 비밀번호를 확인하세요.";
    }

    @GetMapping("/api/authors/{id}")
    public AuthorResponse getAuthorInfo(@PathVariable String id) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                AuthorResponse response = new AuthorResponse(
                        author.getId(),
                        author.getName(),
                        author.getIntroduction(),
                        author.getRole()
                );
                ArrayList<Novel> myNovels = new ArrayList<>();
                for (Novel novel : dataStore.novelList) {
                    if (novel.getAuthor().equals(author.getName())) {
                        myNovels.add(novel);
                    }
                }
                response.setNovels(myNovels);
                return response;
            }
        }
        return null;
    }

    @PostMapping("/api/authors/{id}/NewNovelRegister")
    public String NovelRegister(@PathVariable String id, @RequestBody AuthorNewNovelRequest request) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                Novel newNovel = new Novel(
                        author.getName(),
                        request.getTitle(),
                        request.getTag(),
                        request.getSummary()
                );

                dataStore.novelList.add(newNovel);
                fileService.saveData(); // 💾 파일 저장

                return "작가 " + author.getName() + "님의 새로운 소설 '" + newNovel.getTitle() + "'이(가) 등록되었습니다!";
            }
        }
        return "소설 등록 실패: 작가를 찾을 수 없습니다.";
    }

    @PostMapping("/api/authors/{id}/NewNovelPartRegister")
    public String NovelPartRegister(@PathVariable String id, @RequestBody AuthorNewNovelPartRequest request) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                NovelPart newNovelPart = new NovelPart(
                        request.getNovelTitle(),
                        request.getPartTitle(),
                        request.getContent(),
                        request.getPartNumber()
                );
                newNovelPart.setVip(request.isVip());

                dataStore.novelPartList.add(newNovelPart);
                fileService.saveData(); // 💾 파일 저장

                return "작가 " + author.getName() + "님의 소설 '" + newNovelPart.getNovelTitle() + "'에 새로운 파트 '" + newNovelPart.getPartTitle() + "'이(가) 등록되었습니다!";
            }
        }
        return "소설 파트 등록 실패: 작가를 찾을 수 없습니다.";
    }

    @GetMapping("/api/novels/{novelTitle}")
    public Novel getNovelByTitle(@PathVariable String novelTitle) {
        for (Novel novel : dataStore.novelList) {
            if (novel.getTitle().equals(novelTitle)) {
                return novel;
            }
        }
        return null;
    }

    @GetMapping("/api/novels/{novelTitle}/parts")
    public List<NovelPart> getNovelParts(@PathVariable String novelTitle) {
        List<NovelPart> result = new ArrayList<>();
        for (NovelPart part : dataStore.novelPartList) {
            if (part.getNovelTitle().equals(novelTitle)) {
                result.add(part);
            }
        }
        return result;
    }

    @GetMapping("/api/novels/{novelTitle}/parts/{partNumber}")
    public ResponseEntity<Object> getNovelPart(
            @PathVariable String novelTitle,
            @PathVariable int partNumber,
            @RequestParam(required = false) String viewerId
    ) {
        NovelPart targetPart = null;
        for (NovelPart part : dataStore.novelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                targetPart = part;
                break;
            }
        }

        if (targetPart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 회차입니다.");
        }

        // VIP 전용 확인 로직
        if (targetPart.isVip()) {
            if (viewerId == null || viewerId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요한 VIP 회차입니다.");
            }

            boolean isVipUser = false;
            for (Author author : dataStore.authorList) {
                if (author.getId().equals(viewerId)) {
                    if (author.getRole() == Role.VIP) {
                        isVipUser = true;
                    }
                    break;
                }
            }

            if (!isVipUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("VIP 등급만 열람할 수 있는 유료 회차입니다.");
            }
        }

        return ResponseEntity.ok(targetPart);
    }

    @PostMapping("/api/novels/{novelTitle}/parts/{partNumber}/view")
    public ResponseEntity<Void> increaseViewCount(
            @PathVariable String novelTitle,
            @PathVariable int partNumber,
            @RequestParam(required = false) String viewerId
    ) {
        NovelPart targetPart = null;
        for (NovelPart part : dataStore.novelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                targetPart = part;
                break;
            }
        }

        if (targetPart == null) return ResponseEntity.notFound().build();

        // VIP 권한 확인
        if (targetPart.isVip()) {
            boolean isPass = false;
            if (viewerId != null && !viewerId.isEmpty()) {
                for (Author author : dataStore.authorList) {
                    if (author.getId().equals(viewerId) && author.getRole() == Role.VIP) {
                        isPass = true;
                        break;
                    }
                }
            }
            if (!isPass) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // 조회수 증가
        targetPart.setViewCount(targetPart.getViewCount() + 1);

        // 소설 전체 조회수 증가
        for (Novel novel : dataStore.novelList) {
            if (novel.getTitle().equals(novelTitle)) {
                novel.setTotalCounts(novel.getTotalCounts() + 1);
                break;
            }
        }

        // 수익 정산
        if (targetPart.isVip()) {
            addRevenueToAuthor(novelTitle, 100);
        }

        fileService.saveData(); // 💾 조회수/수익 변경사항 저장
        return ResponseEntity.ok().build();
    }

    // 수익 정산 도우미 함수 (내부 호출용이라 fileService.saveData()는 위에서 호출함)
    private void addRevenueToAuthor(String novelTitle, int amount) {
        String authorName = "";
        for(Novel novel : dataStore.novelList) {
            if(novel.getTitle().equals(novelTitle)) {
                authorName = novel.getAuthor();
                break;
            }
        }

        for(Author author : dataStore.authorList) {
            if(author.getName().equals(authorName)) {
                author.setTotalRevenue(author.getTotalRevenue() + amount);
                System.out.println("💰 수익 발생! " + authorName + " 작가님께 " + amount + "원 적립됨. (총액: " + author.getTotalRevenue() + ")");
                break;
            }
        }
    }

    @GetMapping("/api/authors/{authorId}/revenue")
    public long getAuthorRevenue(@PathVariable String authorId) {
        for(Author author : dataStore.authorList) {
            if(author.getId().equals(authorId)) {
                return author.getTotalRevenue();
            }
        }
        return 0;
    }

    @GetMapping("/api/authors/{id}/manageNovels")
    public List<Novel> manageNovels(@PathVariable String id) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                ArrayList<Novel> myNovels = new ArrayList<>();
                for (Novel novel : dataStore.novelList) {
                    if (novel.getAuthor().equals(author.getName())) {
                        myNovels.add(novel);
                    }
                }
                return myNovels;
            }
        }
        return new ArrayList<>();
    }

    @GetMapping("/api/authors/{id}/manageNovels/{novelTitle}")
    public Novel manageSpecificNovel(@PathVariable String id, @PathVariable String novelTitle) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                for (Novel novel : dataStore.novelList) {
                    if (novel.getAuthor().equals(author.getName()) && novel.getTitle().equals(novelTitle)) {
                        return novel;
                    }
                }
            }
        }
        return null;
    }

    @PutMapping("/api/authors/{id}/manageNovels/{novelTitle}")
    public String updateSpecificNovel(@PathVariable String id, @PathVariable String novelTitle, @RequestBody AuthorNewNovelRequest request) {
        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                for (Novel novel : dataStore.novelList) {
                    if (novel.getAuthor().equals(author.getName()) && novel.getTitle().equals(novelTitle)) {

                        // 정보 업데이트
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

                        fileService.saveData(); // 💾 수정사항 파일 저장
                        return "소설 '" + novelTitle + "'이(가) 성공적으로 업데이트되었습니다!";
                    }
                }
            }
        }
        return "소설 업데이트 실패: 작가나 소설을 찾을 수 없습니다.";
    }

    @GetMapping("/api/novels")
    public List<Novel> getAllNovels() {
        return dataStore.novelList;
    }

    @GetMapping("/api/novels/search")
    public List<Novel> searchNovels(@RequestParam String keyword) {
        List<Novel> result = new ArrayList<>();
        String k = keyword.toLowerCase();
        for (Novel novel : dataStore.novelList) {
            if (novel.getTitle().toLowerCase().contains(k) ||
                    novel.getSummary().toLowerCase().contains(k) ||
                    novel.getAuthor().toLowerCase().contains(k)) {
                result.add(novel);
            }
        }
        return result;
    }

    @GetMapping("/api/comments")
    public List<CommentResponse> getComments(@RequestParam String novelTitle, @RequestParam String partNumber) {
        List<CommentResponse> result = new ArrayList<>();
        for (Comment comment : dataStore.commentList) {
            if (comment.getNovelTitle().equals(novelTitle) && comment.getPartNumber() == Integer.parseInt(partNumber)) {
                CommentResponse response = new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthorName(),
                        comment.getAuthorId()
                );
                result.add(response);
            }
        }
        return result;
    }

    @PostMapping("/api/comments")
    public String addComment(@RequestBody CommentRequest request) {
        // 작가 이름 찾기
        String authorName = "익명";
        for(Author a : dataStore.authorList) {
            if(a.getId().equals(request.getAuthorId())) {
                authorName = a.getName();
                break;
            }
        }

        Comment newComment = new Comment(
                dataStore.commentIdCounter++, // ID 자동 증가
                request.getNovelTitle(),
                request.getPartNumber(),
                request.getAuthorId(),
                authorName,
                request.getContent()
        );

        dataStore.commentList.add(newComment);
        fileService.saveData(); // 💾 파일 저장

        return "댓글이 성공적으로 등록되었습니다!";
    }

    @DeleteMapping("/api/comments/{CommentId}")
    public String deleteComment(@PathVariable Long CommentId, @RequestParam String authorId) {
        for (Comment comment : dataStore.commentList) {
            if (comment.getId().equals(CommentId)) {
                if (comment.getAuthorId().equals(authorId)) {
                    dataStore.commentList.remove(comment);
                    fileService.saveData(); // 💾 파일 저장
                    return "댓글이 성공적으로 삭제되었습니다!";
                } else {
                    return "댓글 삭제 실패: 작성자만 댓글을 삭제할 수 있습니다.";
                }
            }
        }
        return "댓글 삭제 실패: 해당 댓글을 찾을 수 없습니다.";
    }

    @PostMapping("/api/authors/{id}/upgradeToVIP")
    public String upgradeToVIP(@PathVariable String id, @RequestBody VipRequest request) {
        String paymentId = request.getPaymentInfo();
        if (paymentId == null || paymentId.isEmpty()) {
            return "오류: 결제 정보가 없습니다.";
        }

        boolean isPaid = verifyPaymentWithPortOne(paymentId);

        if (!isPaid) {
            return "결제 검증 실패: 결제 내역이 확인되지 않거나, 금액이 맞지 않습니다.";
        }

        for (Author author : dataStore.authorList) {
            if (author.getId().equals(id)) {
                author.setRole(Role.VIP);
                fileService.saveData(); // 💾 등급 변경 저장
                return "축하합니다! " + author.getName() + "님이 정식 VIP 작가가 되셨습니다!";
            }
        }
        return "오류: 작가 정보를 찾을 수 없습니다.";
    }

    // 포트원 결제 검증 (기존 로직 유지)
    private boolean verifyPaymentWithPortOne(String paymentId) {
        try {
            String url = "https://api.portone.io/payments/" + paymentId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "PortOne " + this.apiSecret);

            headers.set("Content-Type", "application/json");

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) return false;

            String status = (String) body.get("status");
            Map<String, Object> amountMap = (Map<String, Object>) body.get("amount");
            Integer totalAmount = (Integer) amountMap.get("total");

            if ("PAID".equals(status) && totalAmount == 5000) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/api/novels/{novelTitle}/{parts}/stats")
    public List<ViewRecommendCount> getViewRecommendCounts(@PathVariable String novelTitle, @PathVariable String parts) {
        List<ViewRecommendCount> result = new ArrayList<>();
        for (NovelPart part : dataStore.novelPartList) {
            if (part.getNovelTitle().equals(novelTitle)) {
                ViewRecommendCount vrc = new ViewRecommendCount(
                        part.getNovelTitle(),
                        part.getPartTitle(),
                        part.getContent(),
                        part.getPartNumber(),
                        part.isVip(),
                        part.getViewCount(),
                        part.getRecommendCount()
                );
                result.add(vrc);
            }
        }
        return result;
    }

    @PostMapping("/api/novels/{novelTitle}/parts/{partNumber}/recommend")
    public String recommendNovelPart(@PathVariable String novelTitle, @PathVariable int partNumber) {
        for (NovelPart part : dataStore.novelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                part.setRecommendCount(part.getRecommendCount() + 1);

                for (Novel novel : dataStore.novelList) {
                    if (novel.getTitle().equals(novelTitle)) {
                        novel.setTotalLikes(novel.getTotalLikes() + 1);
                        break;
                    }
                }
                fileService.saveData(); // 💾 추천수 저장
                return "추천수가 증가되었습니다!";
            }
        }
        return "해당 소설 회차를 찾을 수 없습니다.";
    }

    @GetMapping("/api/novels/{novelTitle}/stats")
    public List<statsResponse> getNovelStats(@PathVariable String novelTitle) {
        List<statsResponse> result = new ArrayList<>();
        for (Novel novel : dataStore.novelList) {
            if (novel.getTitle().equals(novelTitle)) {
                statsResponse stats = new statsResponse(
                        novel.getTitle(),
                        novel.getTotalCounts(),
                        novel.getTotalLikes()
                );
                result.add(stats);
            }
        }
        return result;
    }

    @GetMapping("/api/novels/topViewed")
    public List<Novel> getTopViewedNovels() {
        return dataStore.novelList.stream()
                .sorted((n1, n2) -> Integer.compare(n2.getTotalCounts(), n1.getTotalCounts()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/novels/topRecommended")
    public List<Novel> getTopRecommendedNovels() {
        return dataStore.novelList.stream()
                .sorted((n1, n2) -> Integer.compare(n2.getTotalLikes(), n1.getTotalLikes()))
                .limit(5)
                .collect(Collectors.toList());
    }
}