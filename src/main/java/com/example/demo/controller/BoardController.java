package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
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

    // ⭐ 외부 파일 저장 경로 (PC 환경에 맞게 설정)
    private final String uploadPath = "D:/study/upload_files/";

    @GetMapping
    public Page<Board> getBoards(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        return boardService.getList(page, keyword);
    }

    @Transactional
    @GetMapping("/{id}")
    public Board getBoardDetail(@PathVariable("id") Long id) {
        boardRepository.updateHits(id);
        return boardRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public void createBoard(
            @RequestPart("board") Board board, 
            @RequestPart(value = "file", required = false) MultipartFile file, 
            Principal principal) throws IOException {
        
        Member member = memberRepository.findByUsername(principal.getName()).get();
        board.setMember(member);
        
        // ⭐ 파일 저장 로직 호출
        if (file != null && !file.isEmpty()) {
            saveFileToDisk(board, file);
        }
        
        boardService.save(board);
    }

    @PutMapping("/{id}")
    public void updateBoard(
            @PathVariable("id") Long id,
            @RequestPart("board") Board updateData,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) throws IOException {
        
        Board board = boardRepository.findById(id).orElseThrow();
        
        if (!board.getMember().getUsername().equals(principal.getName())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        board.setTitle(updateData.getTitle());
        board.setContent(updateData.getContent());

        // ⭐ 새 파일이 들어오면 교체
        if (file != null && !file.isEmpty()) {
            saveFileToDisk(board, file);
        }

        boardRepository.save(board);
    }

    // ⭐ 파일 물리적 저장 공통 메서드
    private void saveFileToDisk(Board board, MultipartFile file) throws IOException {
        File saveFolder = new File(uploadPath);
        if (!saveFolder.exists()) saveFolder.mkdirs();

        String originName = file.getOriginalFilename();
        String storedName = UUID.randomUUID().toString() + "_" + originName;
        
        File saveFile = new File(uploadPath, storedName);
        file.transferTo(saveFile);

        board.setOriginFileName(originName);
        board.setStoredFileName(storedName);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long id, Principal principal) {
        boardService.delete(id, principal.getName());
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(boardService.toggleLike(id, principal.getName()));
    }
    
    @GetMapping("/{id}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable("id") Long id) {
        Board board = boardRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(boardLikeRepository.countByBoard(board));
    }
    
    @GetMapping("/{id}/like/status")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return ResponseEntity.ok(false);
        Board board = boardRepository.findById(id).orElseThrow();
        Member member = memberRepository.findByUsername(principal.getName()).get();
        return ResponseEntity.ok(boardLikeRepository.findByMemberAndBoard(member, board).isPresent());
    }
}