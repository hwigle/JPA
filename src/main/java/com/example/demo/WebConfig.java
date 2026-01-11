package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저의 /files/** 경로를 실제 PC의 D:/study/upload_files/ 경로로 연결
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:///D:/study/upload_files/");
    }
}