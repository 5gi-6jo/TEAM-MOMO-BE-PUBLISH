package com.sparta.team6.momo.security.jwt;

import com.sparta.team6.momo.exception.FilterException;
import com.sparta.team6.momo.exception.custom.NeedLoginException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final FilterException filterException;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        NeedLoginException e = new NeedLoginException();
        filterException.unauthorizedException(response, e);
    }
}
