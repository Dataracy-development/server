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
 * Auth 웹 DTO와 Auth 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AuthDevWebMapper {
    // 자체 로그인 웹 요청 DTO -> 자체 로그인 도메인 요청 DTO
    public SelfLoginRequest toApplicationDto(SelfLoginWebRequest webRequest) {
        return new SelfLoginRequest(webRequest.email(), webRequest.password());
    }

    public RefreshTokenRequest toApplicationDto(RefreshTokenWebRequest webRequest) {
        return new RefreshTokenRequest(webRequest.refreshToken());
    }

    public RefreshTokenWebResponse toWebDto(RefreshTokenResponse responseDto) {
        return new RefreshTokenWebResponse(
                responseDto.refreshToken(),
                responseDto.refreshTokenExpiration()
        );
    }

    public ReIssueTokenWebResponse toWebDto(ReIssueTokenResponse responseDto) {
        return new ReIssueTokenWebResponse(
                responseDto.accessToken(),
                responseDto.refreshToken(),
                responseDto.accessTokenExpiration(),
                responseDto.refreshTokenExpiration()
        );
    }
}
