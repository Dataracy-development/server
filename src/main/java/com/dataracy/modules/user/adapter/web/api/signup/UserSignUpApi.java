package com.dataracy.modules.user.adapter.web.api.signup;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User - SignUp", description = "사용자 관련 API - 회원가입")
@RequestMapping("/api/v1")
public interface UserSignUpApi {
    /**
     * 사용자가 자체 회원가입 정보를 제출하여 회원가입을 완료한다.
     * 회원가입이 성공하면 리프레시 토큰이 쿠키와 Redis에 저장되며, 이후 리다이렉트가 수행된다.
     *
     * @param webRequest 회원가입에 필요한 사용자 정보 요청 객체
     * @return 회원가입 성공 시 성공 응답 엔티티
     */
    @Operation(
            summary = "자체 회원가입",
            description = "자체 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "자체 회원가입 성공", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/signup/self", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> signUpUserSelf(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "회원가입 정보",
                    content = @Content(schema = @Schema(implementation = SelfSignUpWebRequest.class))
            )
            @Validated @RequestBody
            SelfSignUpWebRequest webRequest,

            @Parameter(hidden = true)
            HttpServletRequest request,

            @Parameter(hidden = true)
            HttpServletResponse response
    );

    /**
     * 소셜 로그인 온보딩 과정에서 레지스터 토큰과 추가 정보를 사용하여 회원가입을 완료한다.
     *
     * @param registerToken 소셜 회원가입을 위한 레지스터 토큰 값
     * @param webRequest 온보딩 시 입력한 추가 정보(예: 닉네임 등)
     * @return 회원가입 성공 시 리프레시 토큰을 쿠키와 Redis에 저장하고 리다이렉트 응답을 반환한다.
     */
    @Operation(
            summary = "소셜 로그인 후 온보딩 회원가입",
            description = "레지스터 토큰에서 추출한 정보와 추가 정보를 기반으로 소셜 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "소셜 회원가입 성공", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/signup/oauth", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> signUpUserOAuth(
            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "registerToken",
                    required = true,
                    description = "소셜 회원가입을 위한 Register Token",
                    schema = @Schema(type = "string")
            )
            @CookieValue(name = "registerToken")
            String registerToken,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "온보딩 추가 정보 (닉네임, 직업 등)",
                    content = @Content(schema = @Schema(implementation = OnboardingWebRequest.class))
            )
            @Validated @RequestBody
            OnboardingWebRequest webRequest,

            @Parameter(hidden = true)
            HttpServletRequest request,

            @Parameter(hidden = true)
            HttpServletResponse response
    );
}
