package com.sparta.team6.momo.security.jwt;

import com.sparta.team6.momo.dto.TokenDto;
import com.sparta.team6.momo.security.auth.MoMoUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {


    private final TokenInfo tokenInfo;

    private long ACCESS_TOKEN_VALIDITY;

    @Getter
    private long REFRESH_TOKEN_VALIDITY;

    private static final String AUTHORITIES_KEY = "auth";

    private Key key;

    @Autowired
    public TokenProvider(
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            TokenInfo tokenInfo) {
        ACCESS_TOKEN_VALIDITY = accessTokenValidityInSeconds * 1000;
        REFRESH_TOKEN_VALIDITY = refreshTokenValidityInSeconds * 1000;
        this.tokenInfo = tokenInfo;
    }

    @Override
    public void afterPropertiesSet() {
        key = tokenInfo.getKey();
    }


    public TokenDto createToken(Authentication authentication) {
        String accessToken = createAccessToken(authentication);
        String refreshToken = createRefreshToken();
        return TokenDto.withBearer(accessToken, refreshToken);
    }


    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaimsWithoutExpirationCheck(accessToken);
        Collection<? extends GrantedAuthority> authorities = getAuthoritiesFrom(claims);

        MoMoUser principal = new MoMoUser(Long.parseLong(claims.getSubject()), authorities);
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    private String createAccessToken(Authentication authentication) {
        String authorities = getAuthoritiesFrom(authentication);
        String userId = authentication.getName();
        return Jwts.builder()
                .setSubject(userId)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(getExpireDate(ACCESS_TOKEN_VALIDITY))
                .compact();
    }

    private String createRefreshToken() {
        return Jwts.builder()
                .setExpiration(getExpireDate(REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims getClaimsWithoutExpirationCheck(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private String getAuthoritiesFrom(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFrom(Claims claims) {
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Date getExpireDate(long expireIn) {
        long now = (new Date()).getTime();
        return new Date(now + expireIn);
    }
}
