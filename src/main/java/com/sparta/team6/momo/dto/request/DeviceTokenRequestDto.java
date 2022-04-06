package com.sparta.team6.momo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class DeviceTokenRequestDto {
    @NotBlank
    private String token;
}
