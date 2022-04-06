package com.sparta.team6.momo.exception.custom;

import org.springframework.security.core.AuthenticationException;


public class NeedLoginException extends AuthenticationException {

    private static final String message = "로그인이 필요합니다";
    public NeedLoginException() {
        super(message);
    }

}
