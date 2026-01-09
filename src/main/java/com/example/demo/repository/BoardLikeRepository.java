package com.example.demo.repository;

import com.example.demo.domain.Board;
import com.example.demo.domain.BoardLike;
import com.example.demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    
    // 특정 회원이 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<BoardLike> findByMemberAndBoard(Member member, Board board);

    // 특정 게시글의 전체 좋아요 개수 조회
    long countByBoard(Board board);
}