package com.example.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable Spring Security's default login form
        http.formLogin().disable();
        
        // Allow all Vaadin internal requests
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                new AntPathRequestMatcher("/VAADIN/**"),
                new AntPathRequestMatcher("/vaadinServlet/**"),
                new AntPathRequestMatcher("/frontend/**"),
                new AntPathRequestMatcher("/images/**"),
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/signup"),
                new AntPathRequestMatcher("/users/signup"),
                new AntPathRequestMatcher("/")
            ).permitAll()
            .anyRequest().permitAll()  // For now, permit all requests
        );
        
        // Disable CSRF for simplicity in development
        http.csrf().disable();
        
        return http.build();
    }
}