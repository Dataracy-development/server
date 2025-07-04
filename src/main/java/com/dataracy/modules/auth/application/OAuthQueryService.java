package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponseDto;
import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.user.application.UserQueryService;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthQueryService {

    private final UserQueryService userQueryService;
    private final JwtQueryService jwtQueryService;
    private final JwtApplicationService jwtApplicationService;

    /**
     * OAuth2 사용자 신규 여부 확인.
     *
     * @param oAuth2UserInfo 소셜로부터 받은 oAuth2UserInfo
     * @return 신규 사용자 여부
     */
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
        log.info("신규 사용자 등록 토큰 생성 완료 - 이메일: {}", oAuth2UserInfo.getEmail());
        return new RegisterTokenResponseDto(registerToken, jwtQueryService.getRegisterTokenExpirationTime());
    }

    /**
     * 기존 사용자 처리.
     *
     * @param oAuth2UserInfo    OAuth2 유저정보
     */
    public RefreshTokenResponseDto handleExistingUser(OAuth2UserInfo oAuth2UserInfo) {
        return userQueryService.handleExistingUser(oAuth2UserInfo);
    }
}
