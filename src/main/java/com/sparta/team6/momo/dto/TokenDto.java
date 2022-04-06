package com.sparta.team6.momo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;

    public TokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


    public static TokenDto withBearer(String accessToken, String refreshToken) {
        return new TokenDto("Bearer " + accessToken, refreshToken);
    }
}
