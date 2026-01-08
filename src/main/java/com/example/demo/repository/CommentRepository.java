package com.example.demo.repository; // 본인의 패키지 경로에 맞게 수정하세요

import com.example.demo.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 기본적으로 save, findById, delete 등의 메서드가 내장되어 있습니다.
}