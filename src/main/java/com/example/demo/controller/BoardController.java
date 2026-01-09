package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Board;
import com.example.demo.domain.Member;
import com.example.demo.repository.BoardLikeRepository;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    // 1. 게시글 목록 조회 (페이징 & 검색 적용)
    @GetMapping
    public Page<Board> getBoards(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        return boardService.getList(page, keyword);
    }

    // 2. 게시글 상세 조회 (GET)
    @Transactional // 변경 감지나 쿼리 실행을 위해 필요
    @GetMapping("/{id}")
    public Board getBoardDetail(@PathVariable("id") Long id) {
        boardRepository.updateHits(id); // 조회수 증가 함수 호출
        return boardRepository.findById(id).orElseThrow();
    }

    // 3. 게시글 작성 (POST)
    @PostMapping
    public void createBoard(
            @RequestPart("board") Board board, 
            @RequestPart(value = "file", required = false) MultipartFile file, 
            Principal principal) throws IOException {
        // principal.getName()을 통해 로그인한 사용자의 ID(username)를 가져올 수 있음
    	Member member = memberRepository.findByUsername(principal.getName()).get();
        board.setMember(member);
        
        if (file != null && !file.isEmpty()) {
            // 1. 파일 저장 경로 설정 (프로젝트 외부 폴더 권장)
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";
            File saveFolder = new File(projectPath);
            if (!saveFolder.exists()) saveFolder.mkdirs();

            // 2. 파일명 생성 (UUID 사용)
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            
            // 3. 파일 물리적 저장
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            // 4. DB에 정보 저장
            board.setOriginFileName(file.getOriginalFilename());
            board.setStoredFileName(fileName);
        }
        
        boardService.save(board);
    }

    // 4. 게시글 수정 (서비스의 update 로직 호출로 변경)
    @PutMapping("/{id}")
    public void updateBoard(
            @PathVariable("id") Long id,
            @RequestPart("board") Board updateData,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        
        Board board = boardRepository.findById(id).orElseThrow();
        
        // 권한 확인
        if (!board.getMember().getUsername().equals(principal.getName())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 1. 기본 정보 수정
        board.setTitle(updateData.getTitle());
        board.setContent(updateData.getContent());

        // 2. 새 파일이 들어왔을 경우 처리
        if (file != null && !file.isEmpty()) {
            // 기존 파일 삭제 로직 등을 여기에 추가할 수 있습니다.
            // (작성 시 사용했던 파일 저장 로직과 동일하게 구현)
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(projectPath, fileName));
            
            board.setOriginFileName(file.getOriginalFilename());
            board.setStoredFileName(fileName);
        }

        boardRepository.save(board);
    }

    // 5. 게시글 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long id, Principal principal) {
        boardService.delete(id, principal.getName());
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        boolean result = boardService.toggleLike(id, principal.getName());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable("id") Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        long count = boardLikeRepository.countByBoard(board);
        return ResponseEntity.ok(count);
    }
    
    // 좋아요 여부 확인
    @GetMapping("/{id}/like/status")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false); // 로그인 안 했으면 무조건 false
        }

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        Member member = memberRepository.findByUsername(principal.getName()).get();

        // Repository에 findByMemberAndBoard가 이미 있으니 활용
        boolean isLiked = boardLikeRepository.findByMemberAndBoard(member, board).isPresent();
        return ResponseEntity.ok(isLiked);
    }
}