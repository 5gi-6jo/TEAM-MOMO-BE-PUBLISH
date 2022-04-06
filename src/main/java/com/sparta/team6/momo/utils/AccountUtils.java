package com.sparta.team6.momo.utils;

import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.exception.ErrorCode;
import com.sparta.team6.momo.exception.custom.NeedLoginException;
import com.sparta.team6.momo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.sparta.team6.momo.repository.UserRepository;

import javax.security.auth.login.LoginException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountUtils {

    public long getCurUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new NeedLoginException();
        return Long.parseLong(authentication.getName());
    }

}
