package com.dataracy.modules.auth.adapter.web.mapper;

import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.auth.application.dto.request.RefreshTokenRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import org.springframework.stereotype.Component;

/**
 * 개발용 Auth 웹 DTO와 Auth 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AuthDevWebMapper {
    /**
     * 자체 로그인 웹 요청 DTO를 자체 로그인 도메인 요청 DTO로 변환합니다.
     *
     * @param webRequest 자체 로그인 웹 요청 DTO
     * @return 변환된 자체 로그인 도메인 요청 DTO
     */
    public SelfLoginRequest toApplicationDto(SelfLoginWebRequest webRequest) {
        return new SelfLoginRequest(webRequest.email(), webRequest.password());
    }

    /**
     * RefreshTokenWebRequest 객체를 RefreshTokenRequest 도메인 DTO로 변환합니다.
     *
     * @param webRequest 웹 계층의 리프레시 토큰 요청 DTO
     * @return 도메인 계층의 리프레시 토큰 요청 DTO
     */
    public RefreshTokenRequest toApplicationDto(RefreshTokenWebRequest webRequest) {
        return new RefreshTokenRequest(webRequest.refreshToken());
    }

    /**
     * 토큰 재발급 도메인 응답 DTO를 웹 응답 DTO로 변환합니다.
     *
     * @param responseDto 토큰 재발급 도메인 응답 DTO
     * @return 토큰 재발급 웹 응답 DTO
     */
    public RefreshTokenWebResponse toWebDto(RefreshTokenResponse responseDto) {
        return new RefreshTokenWebResponse(
                responseDto.refreshToken(),
                responseDto.refreshTokenExpiration()
        );
    }

    /**
     * 토큰 재발급 도메인 응답 DTO를 토큰 재발급 웹 응답 DTO로 변환합니다.
     *
     * @param responseDto 토큰 재발급 도메인 응답 DTO
     * @return 토큰 재발급 웹 응답 DTO
     */
    public ReIssueTokenWebResponse toWebDto(ReIssueTokenResponse responseDto) {
        return new ReIssueTokenWebResponse(
                responseDto.accessToken(),
                responseDto.refreshToken(),
                responseDto.accessTokenExpiration(),
                responseDto.refreshTokenExpiration()
        );
    }
}
