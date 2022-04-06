package com.sparta.team6.momo.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@Getter
public class TokenInfo implements InitializingBean {
    private final String SECRET;
    private Key key;

    @Autowired
    public TokenInfo(@Value("${jwt.secret}") String secret) {
        SECRET = secret;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

}
