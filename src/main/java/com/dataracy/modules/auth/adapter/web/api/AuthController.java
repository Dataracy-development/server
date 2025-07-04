package com.dataracy.modules.auth.adapter.web.api;

import com.dataracy.modules.auth.adapter.web.mapper.AuthWebMapper;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.SelfLoginUseCase;
import com.dataracy.modules.auth.application.port.in.TokenRedisUseCase;
import com.dataracy.modules.auth.domain.status.AuthSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthWebMapper authWebMapper;

    private final SelfLoginUseCase selfLoginUseCase;
    private final ReIssueTokenUseCase reIssueTokenUseCase;

    private final TokenRedisUseCase tokenRedisUseCase;

    /**
     * 자체로그인을 통해 로그인을 진행한다.
     *
     * @param webRequest 자체로그인 정보(email, password)
     * @param response 리프레시 토큰을 쿠키에 저장
     * @return 로그인 성공
     */
    public ResponseEntity<SuccessResponse<Void>> login(
            SelfLoginWebRequest webRequest,
            HttpServletResponse response
    ) {
        SelfLoginRequest requestDto = authWebMapper.toApplicationDto(webRequest);
        // 자체 로그인 진행
        RefreshTokenResponse responseDto = selfLoginUseCase.login(requestDto);
        // 리프레시 토큰 쿠키 저장
        CookieUtil.setCookie(
                response,
                "refreshToken",
                responseDto.refreshToken(),
                (int) responseDto.refreshTokenExpiration() / 1000
        );
        // 리프레시 토큰 레디스 저장
        tokenRedisUseCase.saveRefreshToken(
                responseDto.userId().toString(),
                responseDto.refreshToken()
        );
        return ResponseEntity.ok(SuccessResponse.of(AuthSuccessStatus.OK_SELF_LOGIN));
    }

    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰과 리프레시 토큰을 발급받아 쿠키에 저장합니다.
     *
     * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰 (쿠키에서 추출)
     * @param response     새로운 토큰을 클라이언트 쿠키에 저장하기 위한 HTTP 응답 객체
     * @return 토큰을 쿠키, 레디스 저장 성공
     */
    public ResponseEntity<SuccessResponse<Void>> reIssueToken(
            String refreshToken,
            HttpServletResponse response
    ) {
        ReIssueTokenResponse responseDto = reIssueTokenUseCase.reIssueToken(refreshToken);
        // 어세스 토큰, 어세스 토큰 만료기간, 리프레시 토큰 쿠키 저장
        setResponseHeaders(response, responseDto);
        // 리프레시 토큰 레디스 저장
        tokenRedisUseCase.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessStatus.OK_RE_ISSUE_TOKEN));
    }

    private void setResponseHeaders(HttpServletResponse response, ReIssueTokenResponse responseDto) {
        CookieUtil.setCookie(response, "accessToken", responseDto.accessToken(), (int) responseDto.accessTokenExpiration() / 1000);
        CookieUtil.setCookie(response, "accessTokenExpiration", String.valueOf(responseDto.accessTokenExpiration()), (int) responseDto.accessTokenExpiration() / 1000);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
    }
}
