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
     * 자체 회원가입을 진행한다.
     *
     * @param webRequest 자체 회원가입 정보
     * @return 회원가입에 성공하여 리프레시 토큰을 쿠키에 저장된다.
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
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param webRequest 닉네임
     * @param response 웹 응답
     * @return void
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
     * 닉네임 중복유무를 확인한다.
     *
     * @param webRequest 유저 닉네임
     * @return void
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
     * 사용자의 비밀번호를 확인하고 성공 여부를 반환합니다.
     *
     * @param userId 비밀번호를 확인할 사용자 ID
     * @param webRequest 비밀번호 확인 요청 정보
     * @return 비밀번호 확인 성공 시 200 OK와 함께 성공 응답을 반환합니다.
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
