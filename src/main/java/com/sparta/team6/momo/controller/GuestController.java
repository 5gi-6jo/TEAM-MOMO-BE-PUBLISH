package com.sparta.team6.momo.controller;

import com.sparta.team6.momo.dto.request.GuestRequestDto;
import com.sparta.team6.momo.dto.response.Success;
import com.sparta.team6.momo.dto.TokenDto;
import com.sparta.team6.momo.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sparta.team6.momo.security.jwt.JwtFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guests")
public class GuestController {

    private final GuestService guestService;

    // 게스트 생성
    @PostMapping
    public ResponseEntity<Success<Object>> connectGuest(@RequestBody GuestRequestDto requestDto) {
        TokenDto tokenDto = guestService.connectGuest(requestDto.getNickname());
        return ResponseEntity.ok()
                .header(JwtFilter.AUTHORIZATION_HEADER, tokenDto.getAccessToken())
                .body(new Success<>("게스트 로그인 성공"));
    }
}
