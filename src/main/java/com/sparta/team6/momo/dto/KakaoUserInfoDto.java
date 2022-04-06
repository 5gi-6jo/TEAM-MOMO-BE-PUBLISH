package com.sparta.team6.momo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
}
