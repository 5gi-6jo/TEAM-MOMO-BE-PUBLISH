package com.sparta.team6.momo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.team6.momo.dto.KakaoUserInfoDto;
import com.sparta.team6.momo.dto.TokenDto;
import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.model.User;
import com.sparta.team6.momo.repository.UserRepository;
import com.sparta.team6.momo.security.auth.MoMoUser;
import com.sparta.team6.momo.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.sparta.team6.momo.exception.ErrorCode.SAME_EMAIL_OTHER_ACCOUNT_EXIST;
import static com.sparta.team6.momo.model.Provider.KAKAO;
import static com.sparta.team6.momo.model.Provider.MOMO;
import static com.sparta.team6.momo.model.UserRole.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public TokenDto kakaoLogin(String code) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        String email = kakaoUserInfo.getEmail();
        
        // 임시 코드
        if (email == null) {
            email = kakaoUserInfo.getId() + "@momo.com";
        }
        //
        
        User kakaoUser = userRepository.findByEmail(email)
                .orElse(null);

        if (kakaoUser == null) {
            String nickname = kakaoUserInfo.getNickname();
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            User user =User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .userRole(ROLE_USER)
                    .provider(KAKAO)
                    .build();
            kakaoUser = userRepository.save(user);
        }

        if (kakaoUser.getProvider() == MOMO) {
            throw new CustomException(SAME_EMAIL_OTHER_ACCOUNT_EXIST);
        }

        log.error("{}", kakaoUser);

        MoMoUser user = new MoMoUser(kakaoUser.getId(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.error("{}", authentication);

        TokenDto tokenDto = tokenProvider.createToken(authentication);
        redisTemplate.opsForValue()
                .set(authentication.getName(), tokenDto.getRefreshToken(), tokenProvider.getREFRESH_TOKEN_VALIDITY(), TimeUnit.MILLISECONDS);

        return tokenDto;
        }


    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "2adbf21838760b4e80d709eeb37a8857");
        body.add("redirect_uri", "https://modumoyeo.com/login/oauth2/code/kakao");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        
        JsonNode node = jsonNode.get("kakao_account").get("email");
        String email;
        if (node == null) email = null;
        else email = node.asText();

        return new KakaoUserInfoDto(id, nickname, email);
    }
}

