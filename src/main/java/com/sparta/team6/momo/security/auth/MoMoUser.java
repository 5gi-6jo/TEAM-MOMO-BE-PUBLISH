package com.sparta.team6.momo.security.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;




@Getter
public class MoMoUser extends User {

    private final Long userId;


    public MoMoUser(Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(String.valueOf(userId), "", authorities);
        this.userId = userId;
    }

    public MoMoUser(Long userId, String password, List<GrantedAuthority> grantedAuthorities) {
        super(String.valueOf(userId), password, grantedAuthorities);
        this.userId = userId;
    }
}
