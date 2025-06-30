package com.dataracy.modules.auth.presentation;

import com.dataracy.modules.auth.application.AuthApplicationService;
import com.dataracy.modules.auth.application.TokenApplicationService;
import com.dataracy.modules.auth.status.AuthSuccessStatus;
import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.dto.response.ReIssueTokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final TokenApplicationService tokenApplicationService;

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
        response.setHeader("Authorization", "Bearer " + responseDto.accessToken());
        response.setHeader("Access-Token-Expire-Time", String.valueOf(responseDto.accessTokenExpiration()));
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
    }

    private void saveRefreshToken(ReIssueTokenResponseDto responseDto) {
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
    }
}
