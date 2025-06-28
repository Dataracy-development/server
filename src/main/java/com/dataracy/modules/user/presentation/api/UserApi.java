package com.dataracy.modules.user.presentation.api;

import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.user.application.dto.request.CheckNicknameRequestDto;
import com.dataracy.modules.user.application.dto.request.OnboardingRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 관련 API")
@RequestMapping("/api/v1")
public interface UserApi {

    /**
     * 🔐 소셜 로그인 후 온보딩 회원가입
     */
    @Operation(
            summary = "소셜 로그인 후 온보딩 회원가입",
            description = "레지스터 토큰과 추가 정보로 소셜 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class)))
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "registerToken",
            required = false,
            description = "소셜 회원가입을 위한 Register Token",
            schema = @Schema(type = "string")
    )
    @PostMapping(value = "/signup/oauth2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> signupUserOAuth2(
            @CookieValue(name = "registerToken", required = false) String registerToken,

            @Validated
            @RequestPart(name = "request")
            @Parameter(
                    description = "온보딩 추가 정보 (닉네임, 직업 등)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OnboardingRequestDto.class))
            )
            OnboardingRequestDto requestDto,

            @RequestPart(name = "profileImage", required = false)
            @Parameter(
                    description = "프로필 이미지 파일 (선택)",
                    required = false,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            MultipartFile profileImage,

            HttpServletResponse response
    );

    /**
     * 🔄 리프레시 토큰 기반 재발급
     */
    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰으로 액세스/리프레시 토큰을 재발급합니다.",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class)))
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "refreshToken",
            required = false,
            description = "Refresh Token (쿠키 전달)",
            schema = @Schema(type = "string")
    )
    @PutMapping("/token/re-issue")
    ResponseEntity<SuccessResponse<Void>> reIssueToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    );

    /**
     * 🔍 닉네임 중복 체크
     */
    @Operation(
            summary = "닉네임 중복 체크",
            description = "닉네임 사용 가능 여부를 확인합니다."
    )
    @ApiResponse(responseCode = "200", description = "닉네임 사용 가능",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class)))
    @PostMapping("/nickname/check")
    ResponseEntity<SuccessResponse<Void>> checkNickname(
            @Validated
            @RequestBody
            @Parameter(
                    description = "닉네임 체크 요청 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CheckNicknameRequestDto.class))
            )
            CheckNicknameRequestDto requestDto
    );
}