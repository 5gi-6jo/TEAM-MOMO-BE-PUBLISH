package com.sparta.team6.momo.dto.response;

import com.sparta.team6.momo.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private boolean isNoticeAllowed;

    public static AccountResponseDto from(User user) {
        return AccountResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isNoticeAllowed(user.isNoticeAllowed())
                .build();
    }
}
