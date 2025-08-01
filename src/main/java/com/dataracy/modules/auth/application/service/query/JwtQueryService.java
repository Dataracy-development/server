package com.dataracy.modules.auth.application.service.query;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtQueryService implements JwtValidateUseCase {
    private final JwtValidatorPort jwtValidatorPort;
    private final JwtProperties jwtProperties;

    /**
     * jwt 토큰 유효성 검사
     *
     * @param token 토큰 문자열
     */
    @Override
    public void validateToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "토큰 유효성 검사 서비스 시작");
        jwtValidatorPort.validateToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "토큰 유효성 검사 서비스 성공", startTime);
    }

    /**
     * JWT 토큰에서 유저 ID 추출
     *
     * @param token 토큰 문자열(어세스 토큰, 리프레시 토큰)
     * @return 유저 ID
     */
    @Override
    public Long getUserIdFromToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "토큰으로부터 유저 아이디 추출 서비스 시작");
        Long userId = jwtValidatorPort.getUserIdFromToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "토큰으로부터 유저 아이디 추출 서비스 성공", startTime);
        return userId;
    }

    /**
     * Register Token에서 OAuth2 제공자 추출
     *
     * @param token 토큰 문자열(리프레시 토큰)
     * @return OAuth2 제공자
     */
    @Override
    public String getProviderFromRegisterToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 추출 서비스 시작");
        String provider = jwtValidatorPort.getProviderFromRegisterToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 추출 서비스 성공", startTime);
        return provider;
    }

    /**
     * Register Token에서 제공자 ID 추출
     *
     * @param token 토큰 문자열(리프레시 토큰)
     * @return 제공자 ID
     */
    @Override
    public String getProviderIdFromRegisterToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 아이디 추출 서비스 시작");
        String providerId = jwtValidatorPort.getProviderIdFromRegisterToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 아이디 추출 서비스 성공", startTime);
        return providerId;
    }

    /**
     * Register Token에서 이메일 추출
     *
     * @param token 토큰 문자열(리프레시 토큰)
     * @return 이메일
     */
    @Override
    public String getEmailFromRegisterToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰으로부터 이메일 추출 서비스 시작");
        String email = jwtValidatorPort.getEmailFromRegisterToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰으로부터 이메일 추출 서비스 성공", startTime);
        return email;
    }

    /**
     * Token에서 Role 추출
     *
     * @param token 토큰 문자열(어세스 토큰, 리프레시 토큰)
     * @return 유저 Role
     */
    @Override
    public RoleType getRoleFromToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "토큰으로부터 유저 role 추출 서비스 시작");
        RoleType role = jwtValidatorPort.getRoleFromToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "토큰으로부터 유저 role 추출 서비스 성공", startTime);
        return role;
    }

    /**
     * Register Token 유효기간 반환
     *
     * @return Register Token 유효기간 (밀리초)
     */
    @Override
    public long getRegisterTokenExpirationTime() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰 유효기간 반환 서비스 시작");
        long registerToken = jwtProperties.getRegisterTokenExpirationTime();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰 유효기간 반환 서비스 성공", startTime);
        return registerToken;
    }

    /**
     * Access Token 유효기간 반환
     *
     * @return Access Token 유효기간 (밀리초)
     */
    @Override
    public long getAccessTokenExpirationTime() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "어세스 토큰 유효기간 반환 서비스 시작");
        long accessToken = jwtProperties.getAccessTokenExpirationTime();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "어세스 토큰 유효기간 반환 서비스 성공", startTime);
        return accessToken;
    }

    /**
     * Refresh Token 유효기간 반환
     *
     * @return Refresh Token 유효기간 (밀리초)
     */
    @Override
    public long getRefreshTokenExpirationTime() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "리프레시 토큰 유효기간 반환 서비스 시작");
        long refreshToken = jwtProperties.getRefreshTokenExpirationTime();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "리프레시 토큰 유효기간 반환 서비스 성공", startTime);
        return refreshToken;
    }

    /**
     * 온보딩 리다이렉트 URL 반환
     *
     * @return 온보딩 리다이렉트 URL
     */
    @Override
    public String getRedirectOnboardingUrl() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "소셜 회원가입 시 추가정보 입력을 위한 온보딩 url 반환 서비스 시작");
        String onboardingUrl = jwtProperties.getRedirectOnboarding();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "소셜 회원가입 시 추가정보 입력을 위한 온보딩 url 반환 서비스 성공", startTime);
        return onboardingUrl;
    }

    /**
     * 로그인 성공 후 리다이렉트 URL 반환
     *
     * @return 로그인 성공 후 리다이렉트 URL
     */
    @Override
    public String getRedirectBaseUrl() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "로그인, 회원가입 완료 후 이동하는 메인 url 반환 서비스 시작");
        String baseUrl = jwtProperties.getRedirectBase();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "로그인, 회원가입 완료 후 이동하는 메인 url 반환 서비스 성공", startTime);
        return baseUrl;
    }
}
