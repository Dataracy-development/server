package com.dataracy.modules.auth.presentation.api;

import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.user.application.dto.request.SelfLoginRequestDto;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RequestMapping("/api/v1")
public interface AuthApi {

    /**
     * 자체로그인을 통해 로그인을 진행한다.
     *
     * @param requestDto 자체로그인 정보(email, password)
     * @param response 리프레시 토큰을 쿠키에 저장
     * @return 로그인 성공
     */
    @Operation(
            summary = "자체 로그인",
            description = "자체 로그인(email, password)을 통해 로그인합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자체로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "로그인 정보 (이메일, 비밀번호)",
                    content = @Content(schema = @Schema(implementation = SelfLoginRequestDto.class))
            )
            @Validated @RequestBody SelfLoginRequestDto requestDto,
            HttpServletResponse response
    );

    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰과 리프레시 토큰을 발급받아 쿠키에 저장합니다.
     *
     * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰 (쿠키에서 추출)
     * @param response     새로운 토큰을 클라이언트 쿠키에 저장하기 위한 HTTP 응답 객체
     * @return 성공 응답
     * @throws Void 리프레시 토큰이 없거나 유효하지 않을 경우 예외 발생
     */
    @Operation(
            summary = "토큰 재발급",
            description = "쿠키의 리프레시 토큰을 통해 토큰 재발급을 시행한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰재발급성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PutMapping(value = "/auth/token/re-issue", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> reIssueToken(

            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "refreshToken",
                    required = true,
                    description = "리프레시 토큰으로 토큰 재발급을 진행한다.",
                    schema = @Schema(type = "string")
            )
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken,

            HttpServletResponse response
    );
}
