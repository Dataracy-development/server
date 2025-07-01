package com.dataracy.modules.auth.presentation;

import com.dataracy.modules.auth.application.AuthApplicationService;
import com.dataracy.modules.auth.application.TokenApplicationService;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponseDto;
import com.dataracy.modules.auth.presentation.api.AuthApi;
import com.dataracy.modules.auth.status.AuthSuccessStatus;
import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.dto.request.SelfLoginRequestDto;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthApplicationService authApplicationService;
    private final TokenApplicationService tokenApplicationService;

    /**
     * 자체로그인을 통해 로그인을 진행한다.
     *
     * @param requestDto 자체로그인 정보(email, password)
     * @param response 리프레시 토큰을 쿠키에 저장
     * @return 로그인 성공
     */
    public ResponseEntity<SuccessResponse<Void>> login(
            SelfLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        RefreshTokenResponseDto responseDto = authApplicationService.login(requestDto);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.ok(SuccessResponse.of(AuthSuccessStatus.OK_SELF_LOGIN));
    }

    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰과 리프레시 토큰을 발급받아 쿠키에 저장합니다.
     *
     * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰 (쿠키에서 추출)
     * @param response     새로운 토큰을 클라이언트 쿠키에 저장하기 위한 HTTP 응답 객체
     * @return 성공 응답
     * @throws Void 리프레시 토큰이 없거나 유효하지 않을 경우 예외 발생
     */
    public ResponseEntity<SuccessResponse<Void>> reIssueToken(
            String refreshToken,
            HttpServletResponse response
    ) {
        ReIssueTokenResponseDto responseDto = authApplicationService.reIssueToken(refreshToken);
        setResponseHeaders(response, responseDto);
        saveRefreshToken(responseDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessStatus.OK_RE_ISSUE_TOKEN));
    }

    private void setResponseHeaders(HttpServletResponse response, ReIssueTokenResponseDto responseDto) {
        CookieUtil.setCookie(response, "accessToken", responseDto.accessToken(), (int) responseDto.accessTokenExpiration() / 1000);
        CookieUtil.setCookie(response, "accessTokenExpiration", String.valueOf(responseDto.accessTokenExpiration()), (int) responseDto.accessTokenExpiration() / 1000);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
    }

    private void saveRefreshToken(ReIssueTokenResponseDto responseDto) {
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
    }
}
