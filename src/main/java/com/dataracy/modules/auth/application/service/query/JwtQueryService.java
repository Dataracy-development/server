package com.dataracy.modules.auth.application.service.query;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtQueryService implements JwtValidateUseCase {
    private final JwtValidatorPort jwtValidatorPort;
    private final JwtProperties jwtProperties;

    /**
     * 주어진 JWT 토큰의 유효성을 검사합니다.
     *
     * @param token 검사할 JWT 토큰 문자열
     */
    @Override
    public void validateToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "토큰 유효성 검사 서비스 시작");
        jwtValidatorPort.validateToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "토큰 유효성 검사 서비스 성공", startTime);
    }

    /**
     * JWT 토큰에서 유저 ID를 추출하여 반환합니다.
     *
     * @param token 유저 정보를 포함한 JWT 토큰 문자열
     * @return 토큰에 포함된 유저 ID
     */
    @Override
    public Long getUserIdFromToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "토큰으로부터 유저 아이디 추출 서비스 시작");
        Long userId = jwtValidatorPort.getUserIdFromToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "토큰으로부터 유저 아이디 추출 서비스 성공", startTime);
        return userId;
    }

    /**
     * 레지스터 토큰에서 OAuth2 제공자 정보를 추출하여 반환합니다.
     *
     * @param token 레지스터 토큰 문자열
     * @return 추출된 OAuth2 제공자 이름
     */
    @Override
    public String getProviderFromRegisterToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 추출 서비스 시작");
        String provider = jwtValidatorPort.getProviderFromRegisterToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 추출 서비스 성공", startTime);
        return provider;
    }

    /**
     * Register Token에서 소셜 제공자 아이디를 추출하여 반환합니다.
     *
     * @param token 소셜 회원가입에 사용되는 레지스터 토큰 문자열
     * @return 추출된 소셜 제공자 아이디
     */
    @Override
    public String getProviderIdFromRegisterToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 아이디 추출 서비스 시작");
        String providerId = jwtValidatorPort.getProviderIdFromRegisterToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰으로부터 소셜 제공자 아이디 추출 서비스 성공", startTime);
        return providerId;
    }

    /**
     * Register Token에서 이메일을 추출하여 반환합니다.
     *
     * @param token 이메일을 추출할 레지스터 토큰 문자열
     * @return 추출된 이메일 주소
     */
    @Override
    public String getEmailFromRegisterToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰으로부터 이메일 추출 서비스 시작");
        String email = jwtValidatorPort.getEmailFromRegisterToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰으로부터 이메일 추출 서비스 성공", startTime);
        return email;
    }

    /**
     * 주어진 토큰에서 유저의 역할(RoleType)을 추출합니다.
     *
     * @param token 역할 정보를 추출할 JWT 토큰 문자열
     * @return 토큰에 포함된 유저의 역할(RoleType)
     */
    @Override
    public RoleType getRoleFromToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "토큰으로부터 유저 role 추출 서비스 시작");
        RoleType role = jwtValidatorPort.getRoleFromToken(token);
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "토큰으로부터 유저 role 추출 서비스 성공", startTime);
        return role;
    }

    /**
     * Register Token의 만료 시간을 밀리초 단위로 반환합니다.
     *
     * @return Register Token의 만료 시간(밀리초)
     */
    @Override
    public long getRegisterTokenExpirationTime() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "레지스터 토큰 유효기간 반환 서비스 시작");
        long registerToken = jwtProperties.getRegisterTokenExpirationTime();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "레지스터 토큰 유효기간 반환 서비스 성공", startTime);
        return registerToken;
    }

    /**
     * Access Token의 만료 시간을 밀리초 단위로 반환합니다.
     *
     * @return Access Token의 만료 시간(밀리초)
     */
    @Override
    public long getAccessTokenExpirationTime() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "어세스 토큰 유효기간 반환 서비스 시작");
        long accessToken = jwtProperties.getAccessTokenExpirationTime();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "어세스 토큰 유효기간 반환 서비스 성공", startTime);
        return accessToken;
    }

    /**
     * Refresh Token의 만료 시간을 밀리초 단위로 반환합니다.
     *
     * @return Refresh Token의 만료 시간(밀리초)
     */
    @Override
    public long getRefreshTokenExpirationTime() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "리프레시 토큰 유효기간 반환 서비스 시작");
        long refreshToken = jwtProperties.getRefreshTokenExpirationTime();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "리프레시 토큰 유효기간 반환 서비스 성공", startTime);
        return refreshToken;
    }

    /**
     * 소셜 회원가입 시 추가 정보 입력을 위한 온보딩 리다이렉트 URL을 반환합니다.
     *
     * @return 온보딩 리다이렉트 URL 문자열
     */
    @Override
    public String getRedirectOnboardingUrl() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "소셜 회원가입 시 추가정보 입력을 위한 온보딩 url 반환 서비스 시작");
        String onboardingUrl = jwtProperties.getRedirectOnboarding();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "소셜 회원가입 시 추가정보 입력을 위한 온보딩 url 반환 서비스 성공", startTime);
        return onboardingUrl;
    }

    /**
     * 로그인 또는 회원가입 완료 후 이동할 기본 리다이렉트 URL을 반환합니다.
     *
     * @return 로그인 또는 회원가입 완료 후 사용자를 리다이렉트할 기본 URL
     */
    @Override
    public String getRedirectBaseUrl() {
        Instant startTime = LoggerFactory.service().logStart("JwtValidateUseCase", "로그인, 회원가입 완료 후 이동하는 메인 url 반환 서비스 시작");
        String baseUrl = jwtProperties.getRedirectBase();
        LoggerFactory.service().logSuccess("JwtValidateUseCase", "로그인, 회원가입 완료 후 이동하는 메인 url 반환 서비스 성공", startTime);
        return baseUrl;
    }
}
