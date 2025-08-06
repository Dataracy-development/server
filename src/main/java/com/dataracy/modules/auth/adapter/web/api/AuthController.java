package com.dataracy.modules.auth.adapter.web.api;

import com.dataracy.modules.auth.adapter.web.mapper.AuthWebMapper;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.domain.status.AuthSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthWebMapper authWebMapper;

    private final SelfLoginUseCase selfLoginUseCase;
    private final ReIssueTokenUseCase reIssueTokenUseCase;

    /**
     * 자체 로그인 요청을 처리하고, 인증에 성공하면 리프레시 토큰을 HTTP 쿠키에 저장한다.
     *
     * 사용자의 이메일과 비밀번호로 자체 로그인을 수행하며, 성공 시 리프레시 토큰을 응답 쿠키로 설정한다.
     *
     * @param webRequest 자체 로그인에 필요한 사용자 정보가 포함된 요청 객체
     * @param response 리프레시 토큰을 쿠키로 저장할 HTTP 응답 객체
     * @return 자체 로그인 성공 시 성공 상태를 포함한 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> login(
            SelfLoginWebRequest webRequest,
            HttpServletResponse response
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[Login] 로그인 API 요청 시작");
        try {
            SelfLoginRequest requestDto = authWebMapper.toApplicationDto(webRequest);
            // 자체 로그인 진행
            RefreshTokenResponse responseDto = selfLoginUseCase.login(requestDto);
            // 리프레시 토큰 쿠키 저장
            long expirationSeconds = responseDto.refreshTokenExpiration() / 1000;
            int maxAge = expirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) expirationSeconds;
            CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), maxAge);
        } finally {
            LoggerFactory.api().logResponse("[Login] 로그인 API 응답 완료", startTime);
        }
        return ResponseEntity.ok(SuccessResponse.of(AuthSuccessStatus.OK_SELF_LOGIN));
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급하고, 해당 토큰 정보를 HTTP 쿠키에 저장합니다.
     *
     * @param refreshToken 클라이언트가 제공한 리프레시 토큰
     * @param response 토큰 정보를 쿠키로 설정할 HTTP 응답 객체
     * @return 토큰 재발급 성공 시 성공 상태의 응답을 반환합니다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> reIssueToken(
            String refreshToken,
            HttpServletResponse response
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[ReIssueToken] 토큰 재발급 API 요청 시작");
        try {
            // 토큰 재발급 진행
            ReIssueTokenResponse responseDto = reIssueTokenUseCase.reIssueToken(refreshToken);
            // 어세스 토큰, 어세스 토큰 만료기간, 리프레시 토큰 쿠키 저장
            setResponseHeaders(response, responseDto);
        } finally {
            LoggerFactory.api().logResponse("[ReIssueToken] 토큰 재발급 API 응답 완료", startTime);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessStatus.OK_RE_ISSUE_TOKEN));
    }

    /**
     * 재발급된 토큰 정보를 기반으로 어세스 토큰, 어세스 토큰 만료기간, 리프레시 토큰을 HTTP 응답 쿠키에 저장합니다.
     *
     * @param response HTTP 응답 객체로, 쿠키가 설정됩니다.
     * @param responseDto 재발급된 토큰 및 만료 정보를 포함하는 DTO입니다.
     */
    private void setResponseHeaders(HttpServletResponse response, ReIssueTokenResponse responseDto) {
        long accessTokenExpirationSeconds = responseDto.accessTokenExpiration() / 1000;
        int accessTokenMaxAge = accessTokenExpirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) accessTokenExpirationSeconds;
        CookieUtil.setCookie(response, "accessToken", responseDto.accessToken(), accessTokenMaxAge);
        CookieUtil.setCookie(response, "accessTokenExpiration", String.valueOf(((long) accessTokenMaxAge) * 1000), accessTokenMaxAge);

        long refreshTokenExpirationSeconds = responseDto.refreshTokenExpiration() / 1000;
        int refreshTokenMaxAge = refreshTokenExpirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) refreshTokenExpirationSeconds;
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), refreshTokenMaxAge);
    }
}
