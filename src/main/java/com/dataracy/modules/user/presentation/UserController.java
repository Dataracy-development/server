package com.dataracy.modules.user.presentation;

import com.dataracy.modules.auth.application.TokenApplicationService;
import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.application.UserApplicationService;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import com.dataracy.modules.user.application.dto.response.LoginResponseDto;
import com.dataracy.modules.user.status.UserSuccessStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;
    private final TokenApplicationService tokenApplicationService;

    /**
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param requestDto 닉네임
     * @return void
     */
    @PostMapping(value = "/public/signup/oauth2")
    public ResponseEntity<SuccessResponse<Void>> signupUserOAuth2(
            @CookieValue(required = false) String registerToken,
            @Validated @RequestBody OnboardingRequestDto requestDto,
            HttpServletResponse response
    ) {
        LoginResponseDto responseDto = userApplicationService.signupUserOAuth2(registerToken, requestDto);
        CookieUtil.setCookie(response, "refreshToken", responseDto.refreshToken(), (int) responseDto.refreshTokenExpiration() / 1000);
        tokenApplicationService.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
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

