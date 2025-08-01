package com.dataracy.modules.user.adapter.web.api.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.adapter.web.mapper.signup.UserSignUpWebMapper;
import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class UserSignUpController implements UserSignUpApi {
    private final UserSignUpWebMapper userSignUpWebMapper;

    private final SelfSignUpUseCase selfSignUpUseCase;
    private final OAuthSignUpUseCase oauthSignUpUseCase;

    /**
     * 자체 회원가입 요청을 처리하고, 성공 시 리프레시 토큰을 쿠키에 저장한다.
     *
     * @param webRequest 자체 회원가입 요청 데이터
     * @param response HTTP 응답 객체
     * @return 회원가입 성공 시 201 Created 상태와 성공 응답을 반환한다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> signUpUserSelf(
            SelfSignUpWebRequest webRequest,
            HttpServletResponse response
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[SignUpUserSelf] 자체 회원가입 API 요청 시작");
        SelfSignUpRequest requestDto = userSignUpWebMapper.toApplicationDto(webRequest);
        // 자체 회원가입 진행
        RefreshTokenResponse responseDto = selfSignUpUseCase.signUpSelf(requestDto);
        // 리프레시 토큰을 쿠키에 저장
        long expirationSeconds = responseDto.refreshTokenExpiration() / 1000;
        int maxAge = expirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) expirationSeconds;
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), maxAge);

        LoggerFactory.api().logResponse("[SignUpUserSelf] 자체 회원가입 API 응답 완료", startTime);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
    }

    /**
     * OAuth 레지스터 토큰과 닉네임 정보를 사용하여 회원가입을 처리하고, 리프레시 토큰을 쿠키에 저장한다.
     *
     * @param registerToken OAuth 회원가입을 위한 레지스터 토큰
     * @param webRequest 닉네임 등 온보딩 정보가 담긴 요청 객체
     * @param response 리프레시 토큰을 쿠키로 설정할 HTTP 응답 객체
     * @return 회원가입 성공 시 201(Created) 상태와 성공 응답을 반환
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> signUpUserOAuth(
            String registerToken,
            OnboardingWebRequest webRequest,
            HttpServletResponse response
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[SignUpUserOAuth] 소셜 회원가입 API 요청 시작");
        OnboardingRequest requestDto = userSignUpWebMapper.toApplicationDto(webRequest);
        // 소셜 회원가입 진행
        RefreshTokenResponse responseDto = oauthSignUpUseCase.signUpOAuth(registerToken, requestDto);
        // 리프레시 토큰을 쿠키에 저장
        long expirationSeconds = responseDto.refreshTokenExpiration() / 1000;
        int maxAge = expirationSeconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) expirationSeconds;
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), maxAge);

        LoggerFactory.api().logResponse("[SignUpUserOAuth] 소셜 회원가입 API 응답 완료", startTime);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
    }
}
