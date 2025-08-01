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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User - SignUp", description = "사용자 관련 API")
@RequestMapping("/api/v1")
public interface UserSignUpApi {
    /**
     * 자체 회원가입을 진행한다.
     *
     * @param webRequest 자체 회원가입 웹 요청 DTO
     * @param response httpServletResponse
     * @return 자체 회원가입 성공 후 쿠키와 레디스에 리프레시 토큰을 저장하고 리다이렉트한다.
     */
    @Operation(
            summary = "자체 회원가입",
            description = "자체 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "자체 회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 DTO 에러(누락 또는 유효성 검증 실패)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
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
            HttpServletResponse response
    );

    /**
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param webRequest 웹 요청 DTO (닉네임)
     * @param response httpServletResponse
     * @return 소셜 온보딩 회원가입 진행 후 쿠키와 레디스에 리프레시 토큰을 저장하고 리다이렉트한다.
     */
    @Operation(
            summary = "소셜 로그인 후 온보딩 회원가입",
            description = "레지스터 토큰에서 추출한 정보와 추가 정보를 기반으로 소셜 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "소셜 회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 DTO 에러(누락 또는 유효성 검증 실패)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "레지스터 토큰 인증 실패(재 회원가입 진행 요청)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
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
            HttpServletResponse response
    );
}
