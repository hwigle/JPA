package com.example.demo.contrller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Board;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor // final이 붙은 필드(레포지토리)를 자동으로 주입
@CrossOrigin(origins = "http://localhost:5173")  // 리액트 접근 허용
public class BoardApiController {
	
	private final BoardRepository boardRepository; // 레포지토리 연결

	// 게시글 저장 API
    @PostMapping
    public Board save(@RequestBody Board board) {
        return boardRepository.save(board);
    }

    // 게시글 목록 조회 API
    @GetMapping
    public List<Board> findAll() {
        return boardRepository.findAll();
    }
    
    // 게시글 삭제 API
    @DeleteMapping("/{id}")  // api/posts/{id} 주소로 오는 삭제 요청 처리
    public void delete(@PathVariable Long id) {
    	boardRepository.deleteById(id);
    }
}
