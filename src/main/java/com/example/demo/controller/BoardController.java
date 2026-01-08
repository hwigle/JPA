package com.example.demo.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Board;
import com.example.demo.domain.Member;
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
    public void createBoard(@RequestBody Board board, Principal principal) {
        // principal.getName()을 통해 로그인한 사용자의 ID(username)를 가져올 수 있음
        Member member = memberRepository.findByUsername(principal.getName()).get();
        board.setMember(member);
        boardService.save(board);
    }

    // 4. 게시글 수정 (서비스의 update 로직 호출로 변경)
    @PutMapping("/{id}")
    public void updateBoard(@PathVariable("id") Long id, @RequestBody Board updateData, Principal principal) {
        Board board = boardRepository.findById(id).orElseThrow();
        
        // 권한 확인: 작성자만 수정 가능
        if (!board.getMember().getUsername().equals(principal.getName())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        board.setTitle(updateData.getTitle());
        board.setContent(updateData.getContent());
        boardRepository.save(board); // JPA가 ID를 확인하고 기존 데이터에 덮어씌움
    }

    // 5. 게시글 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long id, Principal principal) {
        boardService.delete(id, principal.getName());
    }
}