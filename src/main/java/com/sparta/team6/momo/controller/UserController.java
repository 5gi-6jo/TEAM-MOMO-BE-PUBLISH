package com.sparta.team6.momo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.team6.momo.annotation.DTOValid;
import com.sparta.team6.momo.annotation.LogoutCheck;
import com.sparta.team6.momo.dto.*;
import com.sparta.team6.momo.dto.request.DeviceTokenRequestDto;
import com.sparta.team6.momo.dto.request.LoginRequestDto;
import com.sparta.team6.momo.dto.request.NicknameRequestDto;
import com.sparta.team6.momo.dto.request.SignupRequestDto;
import com.sparta.team6.momo.dto.response.AccountResponseDto;
import com.sparta.team6.momo.dto.response.LoginResponseDto;
import com.sparta.team6.momo.dto.response.Success;
import com.sparta.team6.momo.security.jwt.TokenUtils;
import com.sparta.team6.momo.service.UserService;
import com.sparta.team6.momo.service.OAuthService;
import com.sparta.team6.momo.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.sparta.team6.momo.security.jwt.JwtFilter;

import javax.validation.Valid;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;
    private final AccountUtils accountUtils;
    private final TokenUtils tokenUtils;


    // 회원가입
    @PostMapping("/signup")
    @LogoutCheck @DTOValid
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok().body(new Success<>("회원가입 성공"));
    }


    // 로그인
    @PostMapping("/login")
    @LogoutCheck @DTOValid
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequestDto requestDto, BindingResult bindingResult) {


        Map<String, Object> userInfo = userService.loginUser(requestDto.getEmail(), requestDto.getPassword());
        TokenDto jwt = (TokenDto) userInfo.get("tokenDto");
        String nickname = (String) userInfo.get("nickname");
        boolean isNoticeAllowed = (boolean) userInfo.get("isNoticeAllowed");

        ResponseCookie cookie = tokenUtils.createTokenCookie(jwt.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(JwtFilter.AUTHORIZATION_HEADER, jwt.getAccessToken())
                .body(new Success<>("로그인 성공", new LoginResponseDto(nickname, isNoticeAllowed)));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String accessToken,
            @CookieValue(name = "refresh_token") String refreshToken) {

        userService.logout(accessToken.substring(7), refreshToken);
        return ResponseEntity.ok().body(new Success<>());
    }

    // 토큰 재발행
    @GetMapping("/reissue")
    public ResponseEntity<?> reissueToken(
            @RequestHeader("Authorization") String accessToken,
            @CookieValue(value = "refresh_token") String refreshToken) {

        TokenDto reissueTokenDto = userService.reissue(accessToken.substring(7), refreshToken);
        ResponseCookie cookie = tokenUtils.createTokenCookie(reissueTokenDto.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(JwtFilter.AUTHORIZATION_HEADER, reissueTokenDto.getAccessToken())
                .body(new Success<>());
    }

    //로그인 유저 정보
    @GetMapping
    public ResponseEntity<?> getUserInfo() {
        AccountResponseDto userInfo = userService.getUserInfo(accountUtils.getCurUserId());
        return ResponseEntity.ok().body(Success.from(userInfo));
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        TokenDto tokenDto = oAuthService.kakaoLogin(code);
        ResponseCookie cookie = tokenUtils.createTokenCookie(tokenDto.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(JwtFilter.AUTHORIZATION_HEADER, tokenDto.getAccessToken())
                .body(new Success<>());
    }

    //device token 저장
    @PostMapping("/devices")
    public ResponseEntity<Object> updateDeviceToken(@RequestBody @Valid DeviceTokenRequestDto requestDto, BindingResult bindingResult) {
        userService.updateDeviceToken(requestDto.getToken(), accountUtils.getCurUserId());
        return ResponseEntity.ok().body(new Success<>("저장 완료"));
    }

    @PatchMapping("/nicknames")
    @DTOValid
    public ResponseEntity<Object> updateNickname(@RequestBody @Valid NicknameRequestDto requestDto, BindingResult bindingResult) {
        userService.updateNickname(requestDto.getNickname(), accountUtils.getCurUserId());
        return ResponseEntity.ok().body(new Success<>("변경 완료"));
    }


//    @PatchMapping("/password")
//    @DTOValid
//    public ResponseEntity<> changePassword(@RequestBody @Valid PasswordRequestDto requestDto, BindingResult bindingResult) {
//
//    }

}
