package com.sparta.team6.momo.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtFilter jwtFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtFilter.class);
    }
}
