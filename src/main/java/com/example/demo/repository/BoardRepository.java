package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Board;

// JpaRepository를 상속받는 것만으로도 '저장', '삭제', '조회' 기능이 생김.
public interface BoardRepository extends JpaRepository<Board, Long> {
    // 여기에 아무 코드를 적지 않아도 페이징 조회 기능(findAll)이 이미 들어있습니다!
	
	Page<Board> findByTitleContaining(String keyword, Pageable pageable);
}