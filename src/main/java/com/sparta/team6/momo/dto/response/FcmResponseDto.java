package com.sparta.team6.momo.dto.response;

import com.sparta.team6.momo.model.Plan;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class FcmResponseDto {
    private String token;
    private String title;
    private String body;
    private String url;

    @Builder
    public FcmResponseDto(String token, String title, String body, String url) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.url = url;
    }

    private static String reformBody(Plan plan) {
        return String.format("모임시간 %d분 전입니다!\n%s", ChronoUnit.MINUTES.between(LocalDateTime.now(), plan.getPlanDate()), plan.getUrl());
    }

    public static FcmResponseDto of(Plan plan) {
        return FcmResponseDto.builder()
                .token(plan.getUser().getToken())
                .title("모두모여(Momo")
                .body(FcmResponseDto.reformBody(plan))
                .url(plan.getUrl())
                .build();
    }
}
