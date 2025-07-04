package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.TokenRedisUseCase;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.adapter.web.mapper.UserWebMapper;
import com.dataracy.modules.user.adapter.web.request.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.adapter.web.request.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.DuplicateNicknameRequest;
import com.dataracy.modules.user.application.dto.request.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.in.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.SelfSignUpUseCase;
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
    private final TokenRedisUseCase tokenRedisUseCase;

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
        // 리프레시 토큰을 레디스에 저장
        tokenRedisUseCase.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(UserSuccessStatus.CREATED_USER));
    }

    /**
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param webRequest 닉네임
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
        // 리프레시 토큰을 레디스에 저장
        tokenRedisUseCase.saveRefreshToken(responseDto.userId().toString(), responseDto.refreshToken());
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
        // 닉네임 중복 체크
        duplicateNicknameUseCase.validateDuplicatedNickname(requestDto);
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
