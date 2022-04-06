package com.sparta.team6.momo.service;

import com.sparta.team6.momo.dto.response.AccountResponseDto;
import com.sparta.team6.momo.dto.request.SignupRequestDto;
import com.sparta.team6.momo.dto.TokenDto;
import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.exception.ErrorCode;
import com.sparta.team6.momo.model.Account;
import com.sparta.team6.momo.model.User;
import com.sparta.team6.momo.repository.AccountRepository;
import com.sparta.team6.momo.repository.UserRepository;
import com.sparta.team6.momo.security.jwt.TokenProvider;
import com.sparta.team6.momo.security.jwt.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.sparta.team6.momo.exception.ErrorCode.*;
import static com.sparta.team6.momo.model.Provider.KAKAO;
import static com.sparta.team6.momo.model.Provider.MOMO;
import static com.sparta.team6.momo.model.UserRole.ROLE_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {


    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;
    private final AccountRepository accountRepository;

    @Transactional
    public void registerUser(SignupRequestDto requestDto) {
        duplicateEmailCheck(requestDto);
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .userRole(ROLE_USER)
                .provider(MOMO)
                .build();
        userRepository.save(user);
    }

    @Transactional
    public Map<String, Object> loginUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
//        if (user.isLogin()) throw new CustomException(ALREADY_LOGIN_ACCOUNT);
//        else user.setLoginTrue();
        TokenDto tokenDto = createAndSaveToken(authentication);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickname", user.getNickname());
        userInfo.put("isNoticeAllowed", user.isNoticeAllowed());
        userInfo.put("tokenDto", tokenDto);

        return userInfo;
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        if (isRefreshTokenNotEquals(refreshToken, authentication))
            throw new CustomException(INVALID_REFRESH_TOKEN);

        redisTemplate.delete(authentication.getName());

        Long expiration = tokenUtils.getRemainExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        User user = userRepository.findById(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        user.setLoginFalse();
    }


    public TokenDto reissue(String accessToken, String refreshToken) {
        Authentication authentication = getAuthenticationWithCheckToken(refreshToken, accessToken, INVALID_REFRESH_TOKEN);

        if (isRefreshTokenNotEquals(refreshToken, authentication))
            throw new CustomException(INVALID_REFRESH_TOKEN);

        return createAndSaveToken(authentication);
    }

    public AccountResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저를 찾을 수 없습니다")
        );
        return AccountResponseDto.from(user);
    }

    public String getNickname(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getNickname).orElse(null);
    }

    @Transactional
    public void updateDeviceToken(String token, Long accountId) {
        Account savedAccount = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomException(MEMBER_NOT_FOUND)
        );

        if (savedAccount instanceof User) {
            User user = (User) savedAccount;
            if (token != null) user.setNoticeAllowedTrue();
            else user.setNoticeAllowedFalse();
        }

        savedAccount.updateToken(token);
    }

    @Transactional
    public void updateNickname(String nickname, Long accountId) {
        Account savedAccount = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomException(MEMBER_NOT_FOUND)
        );
        savedAccount.updateNickname(nickname);
    }

    private TokenDto createAndSaveToken(Authentication authentication) {
        TokenDto tokenDto = tokenProvider.createToken(authentication);
        redisTemplate.opsForValue()
                .set(authentication.getName(), tokenDto.getRefreshToken(), tokenProvider.getREFRESH_TOKEN_VALIDITY(), TimeUnit.MILLISECONDS);
        return tokenDto;
    }


    private Authentication getAuthenticationWithCheckToken(String validateToken, String accessToken, ErrorCode errorCode) {
        if (!tokenUtils.isTokenValidate(validateToken)) {
            throw new CustomException(errorCode);
        }
        return tokenProvider.getAuthentication(accessToken);
    }


    private boolean isRefreshTokenNotEquals(String refreshToken, Authentication authentication) {
        String savedRefreshToken = redisTemplate.opsForValue().get(authentication.getName());
        return !refreshToken.equals(savedRefreshToken) || ObjectUtils.isEmpty(savedRefreshToken);
    }


    private void duplicateEmailCheck(SignupRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent( (user) -> {
                    if (user.getProvider() == KAKAO) throw new CustomException(SAME_EMAIL_OTHER_ACCOUNT_EXIST);
                    else throw new CustomException(DUPLICATE_EMAIL);
                });

    }

}
