package com.sparta.team6.momo.security.oauth;

import com.sparta.team6.momo.model.User;
import com.sparta.team6.momo.repository.UserRepository;
import com.sparta.team6.momo.security.auth.MoMoUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return createPrincipalDetails(oAuth2User);
    }

    private MoMoUserDetails createPrincipalDetails(OAuth2User oAuth2User) {
        Optional<User> findUser = findUserByEmail(oAuth2User);

        if (findUser.isEmpty()) {
            User user = User.fromKakao(oAuth2User);
            return new MoMoUserDetails(userRepository.save(user), oAuth2User.getAttributes());
        } else {
            return new MoMoUserDetails(findUser.get(), oAuth2User.getAttributes());
        }
    }

    private Optional<User> findUserByEmail(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = String.valueOf(kakaoAccount.get("email"));
        return userRepository.findByEmail(email);
    }
}
