package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.Setter;

//Board.java
@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class) // 자동으로 시간을 기록하기 위해 필요
public class Board {
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	
	 private String title;
	 private String content;
	
	 @CreatedDate // 생성일 자동 기록
	 @Column(updatable = false)
	 private LocalDateTime createDate;
	
	 @ManyToOne
	 @JoinColumn(name = "member_id")
	 private Member member;
	 
	 @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	 @OrderBy("id asc") // 댓글은 등록 순으로 정렬
	 private List<Comment> comments;
	 
	 @Column(columnDefinition = "integer default 0", nullable = false)
    private int hits; // 조회수 필드
	 
	//DB 레벨에서 좋아요 개수를 계산해서 가져오는 가상 컬럼
    @Formula("(SELECT count(*) FROM board_like bl WHERE bl.board_id = id)")
    private int likeCount;
    
    private String originFileName; // 사용자가 올린 파일명 (예: 보노보노.jpg)
    private String storedFileName; // 서버에 저장된 실제 파일명 (예: 20260109_uuid.jpg)
    
    // 댓글 개수를 실시간으로 계산하는 가상 컬럼 추가
    @Formula("(SELECT count(*) FROM comment c WHERE c.board_id = id)")
    private int commentCount;
}