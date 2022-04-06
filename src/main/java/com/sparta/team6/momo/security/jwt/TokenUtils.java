package com.sparta.team6.momo.security.jwt;

import com.sparta.team6.momo.exception.DefaultException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenUtils implements InitializingBean {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenInfo tokenInfo;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        key = tokenInfo.getKey();
    }

    public boolean isTokenBlackList(String jwt) {
        return !ObjectUtils.isEmpty(redisTemplate.opsForValue().get(jwt));
    }

    public boolean isTokenValidate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw DefaultException.fromException(HttpStatus.BAD_REQUEST, "구조적 문제가 있는 JWT 입니다.", e);
        } catch (ExpiredJwtException e) {
            throw DefaultException.fromException(HttpStatus.BAD_REQUEST, "만료된 JWT 입니다.", e);
        } catch (UnsupportedJwtException e) {
            throw DefaultException.fromException(HttpStatus.BAD_REQUEST, "지원되지 않는 형식의 JWT 입니다.", e);
        } catch (SignatureException e) {
            throw DefaultException.fromException(HttpStatus.BAD_REQUEST, "잘못된 서명을 가진 JWT 입니다.", e);
        } catch (IllegalArgumentException e) {
            throw DefaultException.fromException(HttpStatus.BAD_REQUEST, "JWT 가 잘못되었습니다.", e);
        }
    }

    public Long getRemainExpiration(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public ResponseCookie createTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(6000000)
                .build();
    }


    public ResponseCookie delTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .build();
    }



}
