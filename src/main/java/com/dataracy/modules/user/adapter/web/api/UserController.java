package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.adapter.web.mapper.UserWebMapper;
import com.dataracy.modules.user.adapter.web.request.*;
import com.dataracy.modules.user.application.dto.request.*;
import com.dataracy.modules.user.application.port.in.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.application.port.in.user.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.in.user.ConfirmPasswordUseCase;
import com.dataracy.modules.user.application.port.in.user.DuplicateNicknameUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserWebMapper userWebMapper;

    private final SelfSignUpUseCase selfSignUpUseCase;
    private final OAuthSignUpUseCase oauthSignUpUseCase;

    private final DuplicateNicknameUseCase duplicateNicknameUseCase;

    private final ChangePasswordUseCase changePasswordUseCase;
    private final ConfirmPasswordUseCase confirmPasswordUseCase;

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
        SelfSignUpRequest requestDto = userWebMapper.toApplicationDto(webRequest);
        // 자체 회원가입 진행
        RefreshTokenResponse responseDto = selfSignUpUseCase.signUpSelf(requestDto);
        // 리프레시 토큰을 쿠키에 저장
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(),
                (int) responseDto.refreshTokenExpiration() / 1000);
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
        OnboardingRequest requestDto = userWebMapper.toApplicationDto(webRequest);
        // 소셜 회원가입 진행
        RefreshTokenResponse responseDto = oauthSignUpUseCase.signUpOAuth(registerToken, requestDto);
        // 리프레시 토큰을 쿠키에 저장
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(),
                (int) responseDto.refreshTokenExpiration() / 1000);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
    }

    /**
     * 닉네임의 중복 여부를 검사하여 중복이 아닐 경우 성공 응답을 반환한다.
     *
     * @param webRequest 중복 검사를 요청하는 닉네임 정보가 포함된 객체
     * @return 닉네임이 중복되지 않을 경우 성공 상태를 담은 HTTP 200 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> duplicateNickname(
            DuplicateNicknameWebRequest webRequest
    ) {
        DuplicateNicknameRequest requestDto = userWebMapper.toApplicationDto(webRequest);
        duplicateNicknameUseCase.validateDuplicatedNickname(requestDto.nickname());
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME));
    }

    /**
     * 사용자의 비밀번호를 변경한다.
     *
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param webRequest 비밀번호 변경 요청 정보
     * @return 비밀번호 변경 성공 시 200 OK와 성공 응답을 반환
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> changePassword(
            Long userId,
            ChangePasswordWebRequest webRequest
    ) {
        ChangePasswordRequest requestDto = userWebMapper.toApplicationDto(webRequest);
        changePasswordUseCase.changePassword(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_CHANGE_PASSWORD));
    }

    /**
     * 사용자의 비밀번호를 확인한 후 성공 응답을 반환합니다.
     *
     * @param userId 비밀번호를 확인할 대상 사용자의 ID
     * @param webRequest 비밀번호 확인 요청 데이터
     * @return 비밀번호가 올바른 경우 200 OK와 함께 성공 응답을 반환합니다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> confirmPassword(
            Long userId,
            ConfirmPasswordWebRequest webRequest
    ) {
        ConfirmPasswordRequest requestDto = userWebMapper.toApplicationDto(webRequest);
        confirmPasswordUseCase.confirmPassword(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_CONFIRM_PASSWORD));
    }

    /**
     * "/onboarding" 경로에 대한 GET 요청을 처리하여 "onboarding" 뷰 이름을 반환합니다.
     *
     * @return "onboarding" 뷰 이름
     */
    @GetMapping("/onboarding")
    public String onboarding(Model model) {
        return "onboarding";
    }

    /**
     * "/base" 경로에 대한 GET 요청을 처리하여 "base" 뷰 이름을 반환합니다.
     *
     * @return "base" 뷰 이름
     */
    @GetMapping("/base")
    public String base(Model model) {
        return "base";
    }
}
