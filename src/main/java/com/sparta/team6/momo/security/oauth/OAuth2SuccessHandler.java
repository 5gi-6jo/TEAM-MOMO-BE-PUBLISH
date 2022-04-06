package com.sparta.team6.momo.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.team6.momo.dto.response.Success;
import com.sparta.team6.momo.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.sparta.team6.momo.security.jwt.JwtFilter;
import com.sparta.team6.momo.security.jwt.TokenProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        String targetUrl = "http://localhost:3000/oauth2/redirect";
        String targetUrl = "https://www.inflearn.com/";

        TokenDto jwt = tokenProvider.createToken(authentication);
        redisTemplate.opsForValue()
                .set(authentication.getName(), jwt.getRefreshToken(), tokenProvider.getREFRESH_TOKEN_VALIDITY(), TimeUnit.MILLISECONDS);
        setResponseWithJwt(response, jwt);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void setResponseWithJwt(HttpServletResponse response, TokenDto jwt) throws IOException {
        setHeader(response, jwt);
        setBody(response, jwt);
    }

    private void setHeader(HttpServletResponse response, TokenDto jwt) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        response.addHeader(JwtFilter.AUTHORIZATION_HEADER, jwt.getAccessToken());
    }

    private void setBody(HttpServletResponse response, TokenDto jwt) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Success<TokenDto> success = new Success<>(jwt);
        response.getWriter().write(objectMapper.writeValueAsString(success));
    }
}
