package com.dataracy.modules.user.presentation;

import com.dataracy.modules.auth.application.TokenApplicationService;
import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.UserApplicationService;
import com.dataracy.modules.user.application.dto.request.CheckNicknameRequestDto;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfLoginRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfSignupRequestDto;
import com.dataracy.modules.user.application.dto.response.LoginResponseDto;
import com.dataracy.modules.user.presentation.api.UserApi;
import com.dataracy.modules.user.status.UserSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserApplicationService userApplicationService;
    private final TokenApplicationService tokenApplicationService;

    /**
     * 자체로그인을 통해 로그인을 진행한다.
     *
     * @param requestDto 자체로그인 정보(email, password)
     * @param response 리프레시 토큰을 쿠키에 저장
     * @return 로그인 성공
     */
    @Operation(summary = "자체 로그인",
            description = "자체 로그인(email, password)을 통해 로그인합니다.",
            security = {})
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<Void>> login(
            @Validated @RequestBody SelfLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        LoginResponseDto responseDto = userApplicationService.login(requestDto);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.ok(SuccessResponse.of(UserSuccessStatus.OK_SELF_LOGIN));
    }

    /**
     * 자체 회원가입을 진행한다.
     *
     * @param requestDto 자체 회원가입 정보
     * @return Void
     */
    @PostMapping(value = "/signup/self" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<Void>> signupUserSelf(
            @Validated @RequestBody SelfSignupRequestDto requestDto,
            HttpServletResponse response
    ) {
        LoginResponseDto responseDto = userApplicationService.signupUserSelf(requestDto);
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
        LoginResponseDto responseDto = userApplicationService.signupUserOAuth2(registerToken, requestDto);
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
    public String onboarding(@CookieValue(value = "registerToken", required = false) String registerToken, Model model) {
        model.addAttribute("registerToken", registerToken);
        return "onboarding";
    }

    @GetMapping("/base")
    public String base(@CookieValue(value = "refreshToken", required = false) String refreshToken, Model model) {
        model.addAttribute("refreshToken", refreshToken);
        return "base";  // base.html 반환
    }
}

