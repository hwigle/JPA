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
	        // 1. CSRF 보호 비활성화 (H2 콘솔과 API 테스트를 위해)
	        .csrf(csrf -> csrf
	            .ignoringRequestMatchers("/h2-console/**") // H2 콘솔만 제외하거나
	            .disable() // 전체 비활성화
	        )
	        // 2. H2 콘솔이 프레임을 사용할 수 있도록 허용
	        .headers(headers -> headers
	            .frameOptions(frame -> frame.sameOrigin())
	        )
	        // 3. 권한 설정
	        .authorizeHttpRequests(auth -> auth
	        	    .requestMatchers("/", "/index.html", "/join.html", "/api/members/join", "/api/members/me").permitAll()
	        	    // write.html은 로그인이 필요한 페이지로 설정 (선택 사항)
	        	    .requestMatchers("/write.html").authenticated() 
	        	    .anyRequest().authenticated()
	        	)
	        // ... 나머지 로그인 설정 (formLogin 등)
	        .formLogin(login -> login.defaultSuccessUrl("/", true).permitAll());

	    return http.build();
	}

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}