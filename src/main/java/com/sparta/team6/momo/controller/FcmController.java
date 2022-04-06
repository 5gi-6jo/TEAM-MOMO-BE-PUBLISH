package com.sparta.team6.momo.controller;

import com.sparta.team6.momo.dto.response.FcmResponseDto;
import com.sparta.team6.momo.dto.response.Success;
import com.sparta.team6.momo.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sparta.team6.momo.dto.request.FcmRequestDto;
import com.sparta.team6.momo.service.FirebaseCloudMessageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FcmController {
    // 배포 시 삭제할 controller 입니다(테스트용)
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final AccountUtils accountUtils;

    @PostMapping("/fcm")
    public ResponseEntity<Success<Object>> pushMessage(@RequestBody FcmRequestDto requestDto) throws IOException {
        FcmResponseDto responseDto = firebaseCloudMessageService.manualPush(requestDto.getPlanId(), accountUtils.getCurUserId());
        firebaseCloudMessageService.sendMessageTo(
                responseDto.getToken(),
                responseDto.getTitle(),
                responseDto.getBody(),
                responseDto.getUrl());

        return ResponseEntity.ok().body(new Success<>("push message 전송 완료"));
    }
}
