package com.sparta.team6.momo.security.config;

import com.sparta.team6.momo.security.jwt.*;
import com.sparta.team6.momo.security.oauth.OAuth2SuccessHandler;
import com.sparta.team6.momo.security.oauth.Oauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final Oauth2UserService oauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;


    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("ws://https://www.seoultaste.click/ws").permitAll()
                .antMatchers("/meets/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/maps/**").permitAll()
                .antMatchers("/topic/**").permitAll()
                .antMatchers("/guests").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/users/signup").permitAll()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/kakao/**").permitAll()
                .antMatchers("/plans/**").hasAuthority("ROLE_USER")
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v3/api-docs").permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(jwtFilter, jwtExceptionFilter));

//                .and()
//                .oauth2Login()
//                .successHandler(oAuth2SuccessHandler)
//                .userInfoEndpoint()
//                .userService(oauth2UserService);

    }
}
