package com.example.demo.controller;

import com.example.demo.domain.*;
import com.example.demo.dto.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthorController {

    // 임시 저장소 (DB 대신 리스트에 보관)
    private List<Author> authorList = new ArrayList<>();

    private List<Novel> novelList = new ArrayList<>();

    private List<NovelPart> NovelPartList = new ArrayList<>();

    private List<Comment> commentList = new ArrayList<>();
    private Long commentIdCounter = 1L;



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
                        , author.getRole()
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
    public String NovelRegister(@PathVariable String id, @RequestBody AuthorNewNovelRequest request) {
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
    public String NovelPartRegister(@PathVariable String id, @RequestBody AuthorNewNovelPartRequest request) {
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
                //vip 전용 파트 설정
                newNovelPart.setVip(request.isVip());

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

// [AuthorController.java]

    // 🔥 [추가] 이게 없어서 회차 목록이 안 떴던 겁니다! (회차 목록 조회 API)
    @GetMapping("/api/novels/{novelTitle}/parts")
    public List<NovelPart> getNovelParts(@PathVariable String novelTitle) {
        List<NovelPart> result = new ArrayList<>();
        // 전체 파트 중에서, 이 소설 제목이랑 똑같은 것만 골라냄
        for (NovelPart part : NovelPartList) {
            if (part.getNovelTitle().equals(novelTitle)) {
                result.add(part);
            }
        }
        return result; // 골라낸 목록 반환
    }

    // 2. [독자용] 소설 제목과 회차 번호로 '글 내용' 가져오기 (+ VIP 체크 기능 추가)
    @GetMapping("/api/novels/{novelTitle}/parts/{partNumber}")
    public ResponseEntity<Object> getNovelPart(
            @PathVariable String novelTitle,
            @PathVariable int partNumber,
            @RequestParam(required = false) String viewerId // [추가] 누가 보는지 확인!
    ) {
        // 1. 일단 해당 소설 파트를 찾습니다.
        NovelPart targetPart = null;
        for (NovelPart part : NovelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                targetPart = part;
                break;
            }
        }

        // 소설이 없으면 404 에러
        if (targetPart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 회차입니다.");
        }

        // 2. 🔥 [핵심] VIP 전용인지 확인
        if (targetPart.isVip()) {
            // 로그인 안 한 사람이면? -> 차단
            if (viewerId == null || viewerId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요한 VIP 회차입니다.");
            }

            // 로그인 한 사람의 등급 조회
            boolean isVipUser = false;
            for (Author author : authorList) {
                if (author.getId().equals(viewerId)) {
                    if (author.getRole() == Role.VIP) {
                        isVipUser = true;
                    }
                    break;
                }
            }

            // VIP가 아니면? -> 차단 (403 Forbidden 에러를 보냄)
            if (!isVipUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("VIP 등급만 열람할 수 있는 유료 회차입니다.");
            }
        }

        // 3. 통과! 소설 내용 보여줌
        return ResponseEntity.ok(targetPart);
    }
    // [수정됨] 조회수 증가 API (VIP면 볼 때마다 수익 적립!)
    @PostMapping("/api/novels/{novelTitle}/parts/{partNumber}/view")
    public ResponseEntity<Void> increaseViewCount(
            @PathVariable String novelTitle,
            @PathVariable int partNumber,
            @RequestParam(required = false) String viewerId
    ) {
        NovelPart targetPart = null;
        for (NovelPart part : NovelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                targetPart = part;
                break;
            }
        }

        if (targetPart == null) return ResponseEntity.notFound().build();

        // 1. VIP 검문 (권한 확인)
        if (targetPart.isVip()) {
            boolean isPass = false;
            if (viewerId != null && !viewerId.isEmpty()) {
                for (Author author : authorList) {
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

        // 2. 파트 조회수 증가
        synchronized (NovelPartList) {
            targetPart.setViewCount(targetPart.getViewCount() + 1);
        }

        // 3. 전체 조회수 증가
        synchronized (novelList) {
            for (Novel novel : novelList) {
                if (novel.getTitle().equals(novelTitle)) {
                    novel.setTotalCounts(novel.getTotalCounts() + 1);
                    break;
                }
            }
        }

        // 🔥 4. 수익 정산 (여기가 수정됨!)
        // VIP 회차라면? -> 볼 때마다 100원씩 바로 적립!
        if (targetPart.isVip()) {
            addRevenueToAuthor(novelTitle, 100);
        }
        // 일반 회차라면? -> (선택사항) 100 조회수마다 10원 적립 등 규칙 추가 가능
        else {
            // 예: 일반 회차는 돈 안 줌 (원하시면 주석 해제)
            // addRevenueToAuthor(novelTitle, 1);
        }

        return ResponseEntity.ok().build();
    }

    // 💰 [새로 추가할 함수] 작가 찾아서 돈 올려주는 도우미 함수
    private void addRevenueToAuthor(String novelTitle, int amount) {
        // 1. 소설 제목으로 작가 이름 찾기
        String authorName = "";
        for(Novel novel : novelList) {
            if(novel.getTitle().equals(novelTitle)) {
                authorName = novel.getAuthor();
                break;
            }
        }

        // 2. 작가 목록에서 해당 작가 찾아서 적립금 플러스
        for(Author author : authorList) {
            if(author.getName().equals(authorName)) {
                author.setTotalRevenue(author.getTotalRevenue() + amount);
                System.out.println("💰 수익 발생! " + authorName + " 작가님께 " + amount + "원 적립됨. (총액: " + author.getTotalRevenue() + ")");
                break;
            }
        }
    }

    //작가 메인화면에서 내 수익금 확인용
    @GetMapping("/api/authors/{authorId}/revenue")
    public long getAuthorRevenue(@PathVariable String authorId) {
        for(Author author : authorList) {
            if(author.getId().equals(authorId)) {
                return author.getTotalRevenue();
            }
        }
        return 0;
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

    //사이트 내 독자가 보는 메인 페이지
    @GetMapping("/api/novels")
    public List<Novel> getAllNovels() {
        return novelList;
    }

    //소설 검색 기능
    @GetMapping("/api/novels/search")
    public List<Novel> searchNovels(@RequestParam String keyword) {
        List<Novel> result = new ArrayList<>();
        for (Novel novel : novelList) {
            if (novel.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    novel.getSummary().toLowerCase().contains(keyword.toLowerCase()) ||
                    novel.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(novel); // 검색 결과에 추가
            }
        }
        return result;
    }

    //댓글목록 보여주는 기능
    @GetMapping("/api/comments")
    public List<CommentResponse> getComments(@RequestParam String novelTitle, @RequestParam String partNumber) {
        List<CommentResponse> result = new ArrayList<>();
        for (Comment comment : commentList) {
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

    //댓글 작성 기능

    @PostMapping("/api/comments")
    public String addComment(@RequestBody CommentRequest request) {
        // Comment 객체 생성
        Comment newComment = new Comment(
                //리스트크기에 의존하지 않는 id 생성
                commentIdCounter++, // [수정] 번호표 뽑고 1 증가시킴 (절대 중복 안 됨)
                request.getNovelTitle(),
                request.getPartNumber(),
                request.getAuthorId(),
                //request.getAuthorId에 걸맞는 작가이름 찾기
                authorList.stream()
                        .filter(author -> author.getId().equals(request.getAuthorId()))
                        .map(Author::getName)
                        .findFirst()
                        .orElse("익명"),
                request.getContent()
        );

        // 댓글 리스트에 저장
        commentList.add(newComment);

        return "댓글이 성공적으로 등록되었습니다!";
    }

    //댓글 삭제 기능
    @DeleteMapping("/api/comments/{CommentId}")
    public String deleteComment(@PathVariable Long CommentId, @RequestParam String authorId) {
        for (Comment comment : commentList) {
            if (comment.getId().equals(CommentId)) {
                // 작성자 ID가 일치하는지 확인
                if (comment.getAuthorId().equals(authorId)) {
                    commentList.remove(comment);
                    return "댓글이 성공적으로 삭제되었습니다!";
                } else {
                    return "댓글 삭제 실패: 작성자만 댓글을 삭제할 수 있습니다.";
                }
            }
        }
        return "댓글 삭제 실패: 해당 댓글을 찾을 수 없습니다.";
    }


    // 🔥 [수정됨] 결제 검증 로직이 포함된 VIP 업그레이드 기능
    @PostMapping("/api/authors/{id}/upgradeToVIP")
    public String upgradeToVIP(@PathVariable String id, @RequestBody VipRequest request) {

        // 1. 프론트에서 넘어온 영수증 번호 (paymentId) 확인
        String paymentId = request.getPaymentInfo();
        if (paymentId == null || paymentId.isEmpty()) {
            return "오류: 결제 정보가 없습니다.";
        }

        // 2. 포트원 서버에 "진짜 결제됐는지" 물어보기 (검증)
        boolean isPaid = verifyPaymentWithPortOne(paymentId);

        if (!isPaid) {
            return "결제 검증 실패: 결제 내역이 확인되지 않거나, 금액이 맞지 않습니다.";
        }

        // 3. 검증 통과! 이제 안심하고 등급 변경
        for (Author author : authorList) {
            if (author.getId().equals(id)) {
                author.setRole(Role.VIP);
                return "축하합니다! " + author.getName() + "님이 정식 VIP 작가가 되셨습니다!";
            }
        }
        return "오류: 작가 정보를 찾을 수 없습니다.";
    }

    // 🕵️‍♂️ [내부 함수] 포트원 서버와 통신해서 결제 확인하는 녀석
    private boolean verifyPaymentWithPortOne(String paymentId) {
        try {
            // 1. 포트원 V2 API 주소
            String url = "https://api.portone.io/payments/" + paymentId;

            // 2. 헤더에 'API 시크릿' 담기 (여기에 아까 복사한 키를 넣으세요!)
            String apiSecret = "lnXGstx0fz5xaSBMZymZX5lgGAdwoUIXfxTuKRUISlhPRWADp8FGRkS3LbZMuq83cHSwU4QUHSRgZQMb"; // ⚠️ 여기에 진짜 키 붙여넣기!

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "PortOne " + apiSecret);
            headers.set("Content-Type", "application/json");

            // 3. 포트원에 GET 요청 날리기
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            // 4. 응답 분석 (JSON 파싱)
            Map<String, Object> body = response.getBody();
            if (body == null) return false;

            // 5. 핵심 데이터 꺼내기
            String status = (String) body.get("status"); // 결제 상태 (PAID여야 함)

            // 금액 확인 (amount 객체 안에 total이 있음)
            Map<String, Object> amountMap = (Map<String, Object>) body.get("amount");
            Integer totalAmount = (Integer) amountMap.get("total");

            // 6. 최종 검사: "상태가 PAID이고, 금액이 5000원인가?"
            if ("PAID".equals(status) && totalAmount == 5000) {
                return true; // 합격!
            }

        } catch (Exception e) {
            e.printStackTrace(); // 에러 나면 로그 찍기
        }

        return false; // 뭔가 이상하면 무조건 불합격
    }

    //조회수와 추천수를 보여주는 기능
    @GetMapping("/api/novels/{novelTitle}/{parts}/stats")
    public List<ViewRecommendCount> getViewRecommendCounts(@PathVariable String novelTitle, @PathVariable String parts) {
        List<ViewRecommendCount> result = new ArrayList<>();
        for (NovelPart part : NovelPartList) {
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

    //추천수를 증가시키는 기능
    @PostMapping("/api/novels/{novelTitle}/parts/{partNumber}/recommend")
    public String recommendNovelPart(@PathVariable String novelTitle, @PathVariable int partNumber) {
        for (NovelPart part : NovelPartList) {
            if (part.getNovelTitle().equals(novelTitle) && part.getPartNumber() == partNumber) {
                part.setRecommendCount(part.getRecommendCount() + 1);

                //해당 소설의 총추천수도 증가
                for (Novel novel : novelList) {
                    if (novel.getTitle().equals(novelTitle)) {
                        novel.setTotalLikes(novel.getTotalLikes() + 1);
                        break;
                    }
                }

                return "추천수가 증가되었습니다!";
            }
        }
        return "해당 소설 회차를 찾을 수 없습니다.";
    }


    //소설의 총조회수와 총추천수를 보여주는 기능
    @GetMapping("/api/novels/{novelTitle}/stats")
    public List<statsResponse> getNovelStats(@PathVariable String novelTitle) {
        List<statsResponse> result = new ArrayList<>();
        for (Novel novel : novelList) {
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



    // 가장 많이 조회된 소설 Top 5
    @GetMapping("/api/novels/topViewed")
    public List<Novel> getTopViewedNovels() {
        return novelList.stream()
                .sorted((n1, n2) -> Integer.compare(n2.getTotalCounts(), n1.getTotalCounts())) // 스트림에서 정렬 (원본 보존)
                .limit(5)
                .toList();
    }

    // 가장 많이 추천된 소설 Top 5
    @GetMapping("/api/novels/topRecommended")
    public List<Novel> getTopRecommendedNovels() {
        return novelList.stream()
                .sorted((n1, n2) -> Integer.compare(n2.getTotalLikes(), n1.getTotalLikes())) // 스트림에서 정렬 (원본 보존)
                .limit(5)
                .toList();
    }
}


