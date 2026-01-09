package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BoardLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 좋아요를 눌렀는가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 어떤 게시글에 눌렀는가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 생성 편의 메서드
    public static BoardLike createLike(Member member, Board board) {
        BoardLike boardLike = new BoardLike();
        boardLike.setMember(member);
        boardLike.setBoard(board);
        return boardLike;
    }
}