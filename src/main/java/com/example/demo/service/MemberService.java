package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void join(Member member) {
        // 1. 이미 존재하는 아이디인지 확인
        Optional<Member> findMember = memberRepository.findByUsername(member.getUsername());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }

        // 2. 비밀번호 암호화 및 저장 (기존 로직)
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        member.setRole("ROLE_USER");
        memberRepository.save(member);
    }
}