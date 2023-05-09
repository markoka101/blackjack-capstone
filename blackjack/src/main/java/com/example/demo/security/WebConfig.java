//package com.example.demo.security;
//
//import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//@EnableWebMvc
//public class WebConfig implements WebMvcConfigurer
//{
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:4080/")
//                .allowedOrigins("http://localhost:8080/")
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("header1","header2","header3")
//                .exposedHeaders("header1","header2")
//                .allowCredentials(true).maxAge(3600);
//    }
//}