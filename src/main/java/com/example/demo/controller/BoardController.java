package com.example.demo.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
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
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;

    // 1. 게시글 목록 조회 (페이징 & 검색 적용)
    @GetMapping
    public Page<Board> getBoards(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        // 기존 findAll() 대신 서비스의 getList()를 호출합니다.
        return boardService.getList(page, keyword);
    }

    // 2. 게시글 상세 조회 (GET)
    @GetMapping("/{id}")
    public Board getBoard(@PathVariable("id") Long id) {
        return boardService.findById(id);
    }

    // 3. 게시글 작성 (POST)
    @PostMapping
    public void createBoard(@RequestBody Board board, Principal principal) {
        // principal.getName()을 통해 로그인한 사용자의 ID(username)를 가져올 수 있습니다.
        Member member = memberRepository.findByUsername(principal.getName()).get();
        board.setMember(member);
        boardService.save(board);
    }

    // 4. 게시글 수정 (서비스의 update 로직 호출로 변경)
    @PutMapping("/{id}")
    public void updateBoard(@PathVariable("id") Long id, @RequestBody Board board) {
        // 컨트롤러에서 직접 save하지 않고, 서비스에 만들어둔 update 메서드를 사용합니다.
        boardService.update(id, board);
    }

    // 5. 게시글 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long id) {
        boardService.delete(id);
    }
}