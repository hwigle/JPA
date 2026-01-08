package com.example.demo.controller;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 1. 회원가입 API (기존)
    @PostMapping("/join")
    public String join(@RequestBody Member member) {
        memberService.join(member);
        return "회원가입 성공!";
    }

    // 2. 현재 로그인한 사용자 정보 조회 API (추가)
    @GetMapping("/me")
    public Member getMyInfo(Principal principal) {
        // Principal 객체는 현재 로그인한 사용자의 정보를 담고 있습니다.
        if (principal == null) {
            return null; // 로그인이 안 된 경우 null 반환
        }
        
        // 로그인한 사용자의 username(아이디)으로 DB에서 회원 정보를 찾아 반환합니다.
        return memberRepository.findByUsername(principal.getName())
                .orElse(null);
    }
}