package com.example.demo.repository; // 본인의 패키지 경로에 맞게 수정하세요

import com.example.demo.domain.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시글 ID로 댓글을 찾되, 최신순(ID 내림차순)으로 정렬해서 가져오기
    List<Comment> findByBoardIdOrderByIdDesc(Long boardId);
}