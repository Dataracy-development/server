package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponseDto;
import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.user.application.UserQueryService;
import com.dataracy.modules.user.application.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthQueryService {

    private final UserQueryService userQueryService;
    private final JwtUtil jwtUtil;
    private final JwtQueryService jwtQueryService;
    private final JwtApplicationService jwtApplicationService;

    /**
     * OAuth2 사용자 신규 여부 확인.
     *
     * @param oAuth2UserInfo 소셜로부터 받은 oAuth2UserInfo
     * @return 신규 사용자 여부
     */
    @Transactional(readOnly = true)
    public boolean isNewUser(OAuth2UserInfo oAuth2UserInfo) {
        return userQueryService.isNewUser(oAuth2UserInfo);
    }

    /**
     * 신규 사용자 처리.
     *
     * @param oAuth2UserInfo    OAuth2 유저정보
     */
    public RegisterTokenResponseDto handleNewUser(OAuth2UserInfo oAuth2UserInfo) {
        String registerToken = jwtApplicationService.generateRegisterToken(
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId(),
                oAuth2UserInfo.getEmail()
        );
        log.info("신규 사용자 처리 완료: {}", oAuth2UserInfo.getEmail());
        return new RegisterTokenResponseDto(registerToken, jwtQueryService.getRegisterTokenExpirationTime());
    }

    /**
     * 기존 사용자 처리.
     *
     * @param oAuth2UserInfo    OAuth2 유저정보
     */
    public LoginResponseDto handleExistingUser(OAuth2UserInfo oAuth2UserInfo) {
        return userQueryService.handleExistingUser(oAuth2UserInfo);
    }
}
