package com.dataracy.modules.user.presentation.api;

import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.user.application.dto.request.CheckNicknameRequestDto;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfLoginRequestDto;
import com.dataracy.modules.user.application.dto.request.SelfSignupRequestDto;
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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User", description = "사용자 관련 API")
@RequestMapping("/api/v1")
public interface UserApi {

    /**
     * 자체 회원가입을 진행한다.
     *
     * @param requestDto 자체 회원가입 정보
     * @return Void
     */
    @Operation(
            summary = "자체 회원가입",
            description = "자체 회원가입을 통해 회원가입을 합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "자체회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/public/signup/self", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> signupUserSelf(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "회원가입 정보",
                    content = @Content(schema = @Schema(implementation = SelfSignupRequestDto.class))
            )
            @Validated @RequestBody SelfSignupRequestDto requestDto,
            HttpServletResponse response
    );


    /**
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param requestDto 닉네임
     * @return void
     */
    @Operation(
            summary = "소셜 로그인 후 온보딩 회원가입",
            description = "레지스터 토큰에서 추출한 정보와 추가 정보를 기반으로 소셜 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/public/signup/oauth2", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> signupUserOAuth2(
            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "registerToken",
                    required = false,
                    description = "소셜 회원가입을 위한 Register Token",
                    schema = @Schema(type = "string")
            )
            @CookieValue(name = "registerToken", required = false)
            String registerToken,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "온보딩 추가 정보 (닉네임, 직업 등)",
                    content = @Content(schema = @Schema(implementation = OnboardingRequestDto.class))
            )
            @Validated @RequestBody
            OnboardingRequestDto requestDto,
            HttpServletResponse response
    );

    /**
     * 닉네임 중복유무를 확인한다.
     *
     * @param requestDto 유저 닉네임
     * @return void
     */
    @Operation(
            summary = "닉네임 중복체크를 진행한다.",
            description = "제공받은 요청 DTO의 닉네임이 이미 존재하는 닉네임인지 유무를 판단한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 중복되지 않는다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/public/nickname/check", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> checkNickname(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "확인하고자 하는 닉네임",
                    content = @Content(schema = @Schema(implementation = CheckNicknameRequestDto.class))
            )
            @Validated @RequestBody
            CheckNicknameRequestDto requestDto
    );
}
