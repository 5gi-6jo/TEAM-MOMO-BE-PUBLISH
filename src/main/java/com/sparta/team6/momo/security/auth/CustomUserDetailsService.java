package com.sparta.team6.momo.security.auth;

import com.sparta.team6.momo.model.User;
import com.sparta.team6.momo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       return  userRepository.findByEmail(email)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(email + "을 찾을 수 없습니다"));
    }

    private MoMoUser createUser(User user) {
        return new MoMoUser(user.getId(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
