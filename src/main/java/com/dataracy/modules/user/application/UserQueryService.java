package com.dataracy.modules.user.application;

import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.user.application.dto.LoginResponseDto;
import com.dataracy.modules.user.application.dto.RegisterTokenResponseDto;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * OAuth2 사용자 신규 여부 확인.
     *
     * @param oAuth2UserInfo 소셜로부터 받은 oAuth2UserInfo
     * @return 신규 사용자 여부
     */
    public boolean isNewUser(OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.findUserByProviderId(oAuth2UserInfo.getProviderId()) == null;
    }

    /**
     * 신규 사용자 처리.
     *
     * @param oAuth2UserInfo    OAuth2 유저정보
     */
    public RegisterTokenResponseDto handleNewUser(OAuth2UserInfo oAuth2UserInfo) {
        String registerToken = jwtUtil.generateRegisterToken(
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId(),
                oAuth2UserInfo.getEmail()
        );
        log.info("신규 사용자 처리 완료: {}", oAuth2UserInfo.getEmail());
        return new RegisterTokenResponseDto(registerToken, jwtUtil.getRegisterTokenExpirationTime());
    }

    /**
     * 기존 사용자 처리.
     *
     * @param oAuth2UserInfo    OAuth2 유저정보
     */
    public LoginResponseDto handleExistingUser(OAuth2UserInfo oAuth2UserInfo) {
        User existUser = userRepository.findUserByProviderId(oAuth2UserInfo.getProviderId());
        String refreshToken = jwtUtil.generateAccessOrRefreshToken(existUser.getId(), existUser.getRole(), jwtUtil.getRefreshTokenExpirationTime());

        log.info("기존 사용자 처리 완료: {}", existUser.getId());
        return new LoginResponseDto(existUser.getId(), refreshToken, jwtUtil.getRefreshTokenExpirationTime());
    }
}
