package com.sparta.team6.momo.service;

import com.sparta.team6.momo.dto.TokenDto;
import com.sparta.team6.momo.model.Guest;
import com.sparta.team6.momo.model.UserRole;
import com.sparta.team6.momo.repository.GuestRepository;
import com.sparta.team6.momo.security.auth.MoMoUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.sparta.team6.momo.security.jwt.TokenProvider;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenDto connectGuest(String nickname) {
        Guest guest = new Guest(nickname);
        Guest savedGuest = guestRepository.save(guest);

        MoMoUser principal = new MoMoUser(savedGuest.getId(), Collections.singleton(new SimpleGrantedAuthority(UserRole.Authority.GUEST)));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenDto tokenDto = tokenProvider.createToken(authentication);
        redisTemplate.opsForValue()
                .set(authentication.getName(), tokenDto.getRefreshToken(), tokenProvider.getREFRESH_TOKEN_VALIDITY(), TimeUnit.MILLISECONDS);
        return tokenDto;
    }


}
