package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Board;
import com.example.demo.domain.BoardLike;
import com.example.demo.domain.Member;
import com.example.demo.repository.BoardLikeRepository;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardLikeRepository boardLikeRepository;

    public List<Board> findAll() { return boardRepository.findAll(); }
    public void save(Board board) { boardRepository.save(board); }
    public Board findById(Long id) { return boardRepository.findById(id).orElseThrow(); }
    
    public Page<Board> getList(int page, String keyword) {
        // 0번 페이지부터, 10개씩, ID 역순(최신순)으로 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        
        if (keyword == null || keyword.isEmpty()) {
            return boardRepository.findAll(pageable);
        }
        return boardRepository.findByTitleContaining(keyword, pageable);
    }    
    
    @Transactional // 데이터 수정을 위해 추가
    public void update(Long id, Board boardRequest, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // 권한 체크: 게시글 작성자의 username과 현재 로그인한 username 비교
        if (!board.getMember().getUsername().equals(username)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        
        // 제목과 내용만 수정
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        
        // @Transactional이 있으면 save()를 호출하지 않아도 DB에 반영되지만, 
        // 명시적으로 보여주기 위해 호출해도 무방합니다.
        boardRepository.save(board);
    }
    
    @Transactional
    public void delete(Long id, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // 권한 체크
        if (!board.getMember().getUsername().equals(username)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }
    
    @Transactional
    public boolean toggleLike(Long boardId, String username) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        Optional<BoardLike> alreadyLike = boardLikeRepository.findByMemberAndBoard(member, board);

        if (alreadyLike.isPresent()) {
            // 이미 있다면: 좋아요 취소 (삭제)
            boardLikeRepository.delete(alreadyLike.get());
            return false; // 취소됨을 알림
        } else {
            // 없다면: 좋아요 추가 (저장)
            boardLikeRepository.save(BoardLike.createLike(member, board));
            return true; // 추가됨을 알림
        }
    }
}
