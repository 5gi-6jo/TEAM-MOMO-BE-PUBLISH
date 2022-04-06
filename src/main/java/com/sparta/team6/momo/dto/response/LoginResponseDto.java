package com.sparta.team6.momo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String nickname;

    @JsonProperty("isNoticeAllowed")
    private boolean isNoticeAllowed;
}
