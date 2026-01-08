package com.example.demo.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Board;
import com.example.demo.domain.Comment;
import com.example.demo.domain.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @PostMapping("/{boardId}")
    public void saveComment(@PathVariable("boardId") Long boardId, @RequestBody Comment comment, Principal principal) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Member member = memberRepository.findByUsername(principal.getName()).orElseThrow();

        comment.setBoard(board);
        comment.setMember(member);
        commentRepository.save(comment);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long id, Principal principal) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        if (comment.getMember().getUsername().equals(principal.getName())) {
            commentRepository.delete(comment);
        }
    }
}
