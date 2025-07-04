package com.dataracy.modules.user.presentation;

import com.dataracy.modules.auth.application.TokenApplicationService;
import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.UserApplicationService;
import com.dataracy.modules.user.application.dto.request.CheckNicknameRequestDto;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfSignupRequestDto;
import com.dataracy.modules.user.application.dto.response.RefreshTokenResponseDto;
import com.dataracy.modules.user.presentation.api.UserApi;
import com.dataracy.modules.user.status.UserSuccessStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserApplicationService userApplicationService;
    private final TokenApplicationService tokenApplicationService;

    /**
     * 자체 회원가입을 진행한다.
     *
     * @param requestDto 자체 회원가입 정보
     * @return Void
     */
    public ResponseEntity<SuccessResponse<Void>> signupUserSelf(
            SelfSignupRequestDto requestDto,
            HttpServletResponse response
    ) {
        RefreshTokenResponseDto responseDto = userApplicationService.signupUserSelf(requestDto);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
    }

    /**
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param requestDto 닉네임
     * @return void
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> signupUserOAuth2(
            String registerToken,
            OnboardingRequestDto requestDto,
            HttpServletResponse response
    ) {
        RefreshTokenResponseDto responseDto = userApplicationService.signupUserOAuth2(registerToken, requestDto);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(),
                (int) responseDto.refreshTokenExpiration() / 1000);
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
    }

    /**
     * 닉네임 중복유무를 확인한다.
     *
     * @param requestDto 유저 닉네임
     * @return void
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> checkNickname(
            CheckNicknameRequestDto requestDto
    ) {
        userApplicationService.checkNickname(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME));
    }

    @GetMapping("/onboarding")
    public String onboarding(Model model) {
        return "onboarding";
    }

    @GetMapping("/base")
    public String base(Model model) {
        return "base";  // base.html 반환
    }
}

