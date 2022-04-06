package com.sparta.team6.momo.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요")
    private String checkPassword;

    @AssertTrue(message = "입력한 비밀번호와 같지 않습니다")
    public boolean isSamePwd() {
        return password.equals(checkPassword);
    }
}
