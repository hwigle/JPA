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
            .csrf(csrf -> csrf.disable()) // API 테스트를 위해 CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
        		.requestMatchers("/", "/index.html", "/join.html", "/api/members/join").permitAll() 
        	    .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .defaultSuccessUrl("/", true) // 로그인 성공 시 메인페이지로
                .permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}