package com.sparta.team6.momo.controller;

import com.sparta.team6.momo.dto.response.Success;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sparta.team6.momo.dto.response.MeetResponseDto;
import com.sparta.team6.momo.service.MeetService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meets")
public class MeetController {

    private final MeetService meetService;

    // 지도 주소 접근시 해당 모임 id 반환
    @GetMapping("/{randomUrl}")
    public ResponseEntity<Success<MeetResponseDto>> getPlanIdFromUrl(@PathVariable("randomUrl") String url) {
        MeetResponseDto responseDto = meetService.getPlanInfo(url);
        return ResponseEntity.ok().body(new Success<>(responseDto));
    }
}
