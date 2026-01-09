package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 실습을 위해 CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                // 1. 누구나 들어올 수 있게 문을 열어줘야 하는 곳들
                .requestMatchers("/", "/index.html", "/header.html").permitAll() 
                .requestMatchers("/join.html").permitAll() // ⭐ 회원가입 페이지 개방!
                .requestMatchers("/login").permitAll()    // 기본 로그인 페이지 개방
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                
                // 2. 백엔드 API 권한 설정
                .requestMatchers("/api/members/join").permitAll() // ⭐ 회원가입 처리 API 개방!
                .requestMatchers("/api/members/me").permitAll()   // 로그인 체크 API 개방
                .requestMatchers("/api/boards/**").permitAll()    // 게시판 조회 개방
                
                // 3. 나머지는 로그인 해야만 접근 가능
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // .loginPage()를 적지 않으면 스프링 기본 로그인 화면이 나옵니다.
                .defaultSuccessUrl("/", true) // 로그인 성공 시 메인으로 이동
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}